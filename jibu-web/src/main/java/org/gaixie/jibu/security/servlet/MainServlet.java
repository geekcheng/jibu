package org.gaixie.jibu.security.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.service.SettingService;
import org.gaixie.jibu.utils.BundleHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于登录成功后生成主窗口页面，加载应用共用的javascript文件及CSS文件。
 * <p>
 * MainServlet 也响应主窗口的一些ajax请求，如菜单树加载。
 */
@ Singleton public class MainServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(MainServlet.class);
    @Inject private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        SettingService settingService = injector.getInstance(SettingService.class);
        HttpSession ses = req.getSession(false);
        String username = (String)ses.getAttribute("username");

        // Web Server 重启会时 session 会保持，但之前 Guice 生成的实例可能已经回收
        // 这时需要清空 session ，强制用户进行登录
        if(username != null && settingService == null) {
            logger.warn("Cant get old instance created by guice, username="+username 
                        + ", SettingService = "+settingService);
            resp.sendRedirect("Login.x?ci=logout&reason=serviceReloaded");
            return;
        }

        List<Setting> settings = settingService.findByUsername(username);

        String theme = "";
        String layout = null;
        // 首先看用户是否选择过语言。
        // 如果选择过，直接读取选择的语言，以Locale对象放入session。
        // 如果没有选择，取当前浏览器支持的默认locale
        for (Setting setting :settings) {
            if ("theme".equals(setting.getName())) {
                if (!"blue".equals(setting.getValue())) {
                    theme = "-"+setting.getValue();
                }
            } else if ("layout".equals(setting.getName())) {
                layout = setting.getValue();
            } else if ("language".equals(setting.getName())) {
                ses.setAttribute("locale", ServletUtils.convertToLocale(setting.getValue()));
            }
        }

        Locale locale = ServletUtils.getLocale(req);
        BundleHandler rb = new BundleHandler(locale);

        PrintWriter pw = resp.getWriter();
        pw.println(ServletUtils.head("Jibu")+
                   ServletUtils.favicon()+
                   ServletUtils.css("libs/ext/resources/css/ext-all"+theme+".css")+
                   ServletUtils.css("css/jibu-all.css")+
                   ServletUtils.css("js/"+layout.toLowerCase()+"/layout.css")+
                   ServletUtils.body());
	loadingDiv(rb.get("loading.message.001"),pw);
        pw.println(ServletUtils.div("header",""));

	loading(rb.get("loading.message.002"),"5",pw);
        pw.println(ServletUtils.javascript("libs/ext/ext-all.js"));

	loading(rb.get("loading.message.003"),"40",pw);
        pw.println(ServletUtils.javascript("libs/jquery.js"));
        pw.println(ServletUtils.javascript("libs/highcharts/highcharts.js"));
        pw.println(ServletUtils.javascript("libs/highcharts/exporting.js"));
        pw.println(ServletUtils.javascript("ext-ux/ext-ux-all.js"));

	loading(rb.get("loading.message.004"),"65",pw);
	pw.println(loadData(authService,req));

	loading(rb.get("loading.message.005"),"80",pw);
        pw.println(ServletUtils.javascript("js/"+layout.toLowerCase()+"/layout.js"));

	loading(rb.get("loading.message.006"),"85",pw);
        pw.println(ServletUtils.javascript("libs/ext/locale/ext-lang-"+ getLanguage(locale)+".js")+
                   ServletUtils.javascript("js/status-"+ getLanguage(locale)+".js")+
                   ServletUtils.javascript("js/"+layout.toLowerCase()+"/layout-"+ getLanguage(locale)+".js"));
	loading(rb.get("loading.message.006"),"100",pw);
	loadingHidden(pw);
        pw.println(ServletUtils.footer());
	pw.println(ServletUtils.closeHtml());
        pw.close();
    }

    public String loadData(AuthorityService authService,
                           HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        // 用一个有序的 TreeSet，使其可以被Mock测试，性能损失可以被忽略。
        Set mod = new TreeSet();
        Locale locale = ServletUtils.getLocale(req);
        BundleHandler rb = new BundleHandler(locale);

        HttpSession ses = req.getSession(false);
        Map<String,String> map =
            authService.findMapByUsername((String)ses.getAttribute("username"));

        /*------------------------------------------------------------------------------------
         * 算法：
         * 提高初次载入的效率，需要用一遍循环得到
         * 1）用于菜单加载的 树形数据 （json格式）。
         *    放入<script> 标签内，直接返回，无须第二次 ajax请求。
         *    pre:  前一个节点的层数
         *    cur:  当前节点的层数
         *    dist: 上一个节点和当前节点的层距
         *    index: 用于判断是否是第一个节点
         * 2) 根据权限加载所需的 js文件。（确切的说应该是根据权限加载顶层子系统 js文件)。
         *    如果权限 key = system.administration.pm 。
         *    根据约定，一定有一个 system-all.js 文件在 /js/system/ 目录下。
         *    应该只加载此文件。同级目录及该目录下所有 js文件的压缩打包在各系统的 pom 中配置。
         *------------------------------------------------------------------------------------*/
        int pre =0;
        int index =0;
        Iterator iter = map.entrySet().iterator();
        // 定义一个全局变量，用来保存 json数据。
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("jibu={};\n");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            // 去掉头两个字符（初始化菜单的时候，约定好前两位必须用数字，用于子系统在菜单中的排序）
            key = key.substring(2);
            String val = (String)entry.getValue();

            // text: key 应该是国际化过的串，用于显示，以menu为前缀。
            String node = "{url:\""+key+"\",text:\""+rb.get(key)+"\",leaf:"
                +(("#".equals(val)) ? "false" : "true}");

            String[] path = key.split("\\.");
            int cur = path.length;

            if (!"#".equals(val)) {
                // s = 要加载的js 文件路经
                String s = key.substring(0,key.indexOf("."));
                mod.add("js/"+s+"/"+s);
            }

            int dist = pre - cur;

            if (dist < 0) {                      // 层数在增加
                // 第一个节点按照JSON格式需要特殊处理，注意在这里开始给 JibuNavs赋值
                if (index == 0) sb.append("jibu.navData = ["+node);
                else sb.append(",children:["+node);
            } else if (dist >0) {                // 层数在减少
                for (int i=0;i<dist;i++) {
                    sb.append("]}");
                }
                sb.append(","+node);
            } else {                             // 层数没变化
                sb.append(","+node);
            }
            pre = cur;
            index++;
        }
        for (int i=0;i<pre-1;i++) {
            sb.append("]}");
        }
        if (map.size()>0) sb.append("];\n");
        sb.append("jibu.locale='"+getLanguage(locale)+"';\n");
        sb.append("jibu.username='"+(String)ses.getAttribute("username")+"';\n");
        sb.append("</script>\n");

        sb.append("<script type=\"text/javascript\" src=\"js/common/common-all.js\"></script>\n");
        // 输出拥有权限的顶层子系统 js 文件。
        Iterator ite = mod.iterator();
        String module;
        while (ite.hasNext()){
            module = (String)ite.next();
            sb.append("<script type=\"text/javascript\" src=\""+module+"-all.js\"></script>\n");
        }

        return sb.toString();
    }

    private void loadingDiv(String msg, PrintWriter pw) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"loading\" style=\"position: absolute;width: 100%;height: 100%;z-index: 1000;background-color: #fff\">");
        sb.append("<div class=\"cmsg\" style=\"margin: 1em\">");
        sb.append("<div id=\"loading-msg\" style=\"font-size:12px;font-weight: bold\">"+msg+"</div>");
        sb.append("<div class=\"lpb\" style=\"margin: 2px 0;border: 1px solid #949dad;width: 300px;height: 0.5em;overflow: hidden;padding: 1px\">"+
                  "<div id=\"lpt\" style=\"background: #d4e4ff;width: 0;height: 100%;font-size: 0\"></div>"+
                  "</div></div></div>");
        pw.println(sb.toString());
        pw.flush();
    }

    private void loading(String msg, String percent, PrintWriter pw) {
        pw.println("<script type=\"text/javascript\">\n"+
                   "  document.getElementById('loading-msg').innerHTML = '"+msg+"...';\n"+
                   "  document.getElementById('lpt').style.width = '"+percent+"%';\n"+
                   "</script>");
        pw.flush();
    }

    private void loadingHidden(PrintWriter pw) {
        pw.println("<script type=\"text/javascript\">\n"+
                   "  document.getElementById('loading').style.visibility = 'hidden';\n"+
                   "</script>");
        pw.flush();
    }

    private String getLanguage(Locale locale) {
        if (locale.getLanguage().equals(new Locale("zh", "", "").getLanguage())) {
            return locale.toString();
        }
        return locale.getLanguage();
    }

    public void doPost(HttpServletRequest req,  HttpServletResponse resp)
        throws IOException{
        doGet(req, resp);
    }
}
