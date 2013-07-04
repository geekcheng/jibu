package org.gaixie.jibu.security.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.LoginException;
import org.gaixie.jibu.security.service.LoginService;
import org.gaixie.jibu.security.service.SettingService;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.BundleHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应用户设置相关操作的请求。
 * <p>
 */
@Singleton
public class SettingServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(MainServlet.class);

    @Inject
    private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String rst = null;
        if ("formLoad".equals(req.getParameter("ci"))) {
            rst = formLoad(req,injector);
        } else if ("settingLoad".equals(req.getParameter("ci"))) {
            rst = settingLoad(req,injector);
        } else if ("settingUpdate".equals(req.getParameter("ci"))) {
            rst = settingUpdate(req,injector);
        }

        PrintWriter pw = resp.getWriter();
        pw.println(rst);
        pw.close();
    }

    public void doPost(HttpServletRequest req,  HttpServletResponse resp)
        throws IOException{
        doGet(req, resp);
    }

    protected String formLoad(HttpServletRequest req,
                              Injector injector) {
        SettingService settingService = injector.getInstance(SettingService.class);
        UserService userService = injector.getInstance(UserService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        HttpSession ses = req.getSession(false);
        String username = (String)ses.getAttribute("username");
        List<Setting> settings = settingService.findByUsername(username);
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        // 这里取到的Setting 包含默认值，但 language除外，它取的是客户端 locale
        // 参见 patch01.sql 的 language 初始化。
        for (Setting s : settings) {
            String key = "setting."+s.getName()+"."+s.getValue();
            s.setValue(rb.get(key));
        }

        Map<String, Object> data = new HashMap<String, Object>();
        User user = userService.get(username);
        data.put("User.id",user.getId());
        data.put("User.username",user.getUsername());
        data.put("User.fullname",user.getFullname());
        data.put("User.emailaddress",user.getEmailaddress());

        jsonMap.put("success", true);
        jsonMap.put("data", new JSONObject(data));
        jsonMap.put("settings", new JSONArray(settings));
        return (new JSONObject(jsonMap)).toString();
    }

    protected String settingLoad(HttpServletRequest req,
                                 Injector injector) {
        SettingService settingService = injector.getInstance(SettingService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String name = req.getParameter("settings.name");
        List<Setting> settings = settingService.findByName(name);
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        for (Setting s : settings) {
            String key = "setting."+s.getName()+"."+s.getValue();
            s.setValue(rb.get(key));
        }

        jsonMap.put("success", true);
        //            jsonMap.put("data", new JSONObject(data));
        jsonMap.put("settings", new JSONArray(settings));
        return (new JSONObject(jsonMap)).toString();
    }

    protected String settingUpdate(HttpServletRequest req,
                                   Injector injector) {
        SettingService settingService = injector.getInstance(SettingService.class);
        UserService userService = injector.getInstance(UserService.class);
        LoginService loginService = injector.getInstance(LoginService.class);
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        try {
            HttpSession ses = req.getSession(false);
            String username = (String)ses.getAttribute("username");
            String oldPasswd = req.getParameter("oldpassword");
            loginService.login(username,oldPasswd);

            // 取当前 username 的id，不能取前段传过来的User.id，安全原因
            User u = userService.get(username);
            User user = ServletUtils.httpToBean(User.class,req);
            user.setId(u.getId());

            List<Integer> list = new ArrayList<Integer>();
            String[] sids = req.getParameterValues("settings.id");
            for (String sid : sids) {
                if(!sid.isEmpty()) {
                    list.add(Integer.valueOf(sid));
                }
            }
            settingService.updateMe(list, user);
            jsonMap.put("success", true);
            jsonMap.put("message", rb.get("message.001"));
        } catch (LoginException le) {
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("setting.message.001"));
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }
}
