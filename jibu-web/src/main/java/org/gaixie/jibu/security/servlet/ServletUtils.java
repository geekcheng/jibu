package org.gaixie.jibu.security.servlet;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.utils.BeanConverter;
import org.gaixie.jibu.security.model.Criteria;

import java.beans.IntrospectionException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Servlet 工具类。
 * <p>
 */
public class ServletUtils {

    public static final String DOCTYPE =
        "<!DOCTYPE html>";
    //        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">";

    /**
     * 开始 html, head 标签。
     * <p>
     * @param title title 标签内容。
     * @return {@code <html><head><title>title</title><meta ..../>}
     */
    public static String head(String title) {
        return(DOCTYPE + "\n" +
               "<html>\n" +
               "<head><title>" + title + "</title>\n" +
               "  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />\n");
    }

    /**
     * 加载 favicon.ico 文件的标签。
     * <p>
     * @return {@code <link rel=\"shortcut icon\" href=\"http://gaixie.org/favicon.ico?1\" type=\"image/x-icon\"/>}
     */
    public static String favicon() {
        return("  <link rel=\"shortcut icon\" href=\"http://gaixie.org/favicon.ico?1\" type=\"image/x-icon\"/>\n");
    }

  
    /**
     * 加载 css 文件的标签。
     * <p>
     * @param path 文件路径。
     * @return {@code <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"path\" />}
     */
    public static String css(String path) {
        return("  <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\""+path+"\" />\n");
    }

    /**
     * 加载 javascript 文件的标签。
     * <p>
     * @param path 文件路径。
     * @return {@code <script type=\"text/javascript\" src=\"path\"></script>}
     */
    public static String javascript(String path) {
        return("<script type=\"text/javascript\" src=\""+path+"\"></script>\n");
    }

    /**
     * 封闭 head，开始 body 标签。
     * <p>
     * @return {@code </head><body>}
     */
    public static String body() {
        return("</head>\n"+
               "<body>\n");
    }

    /**
     * 封闭 body 标签。
     * <p>
     * @return {@code </body>}
     */
    public static String footer() {
        return("</body>\n");
    }

    /**
     * 封闭 html 标签。
     * <p>
     * @return {@code </html>}
     */
    public static String closeHtml() {
        return("</html>");
    }

    /**
     * 返回一个div 。
     * <p>
     * @param id div 的 id。
     * @param content div 的内容。
     * @return {@code <div id=\"name\">content</div>}
     */
    public static String div(String id,String content) {
        return("  <div id=\""+id+"\">"+content+"</div>\n");
    }

    /**
     * 将 HttpServletRequest 中的值按照约定自动装入指定的 Javabean。
     * <p>
     * 如果 request 中的 name 满足 ClassName.property 的格式，会自动
     * 取出对应的 value 并装入Bean 相应属性 。如：User.password=123456，
     * 那么会将 123456 set 到 User 的 password 属性中。 <br>
     * @param cls 要生成的 Bean Class。
     * @param req 要处理的 Request。
     * @return 生成的 Javabean
     */
    public static <T> T httpToBean(Class<T> cls, HttpServletRequest req)
        throws JibuException {
        HashMap map = new HashMap();
        Enumeration names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = req.getParameter(name);
            int idx = name.lastIndexOf('.');
            if (!value.trim().isEmpty() && idx > 0) {
                map.put(name.substring(idx+1), value);
            }
        }
        T bean = BeanConverter.mapToBean(cls, map);
        return bean;
    }

    /**
     * 将 HttpServletRequest 中的值按照约定自动装入指定的 Javabean。
     * <p>
     * 如果 request 中的 name 满足 ClassName.property 的格式，会自动
     * 取出对应的 value 并装入Bean 相应属性 。如：User.password=123456，
     * 那么会将 123456 set 到 User 的 password 属性中。 <br>
     * @param cls 要生成的 Bean Class。
     * @param req 要处理的 Request。
     * @param hasPre 判断 req 中参数名是否用类名做前缀， true 含前缀，false 不含前缀
     * @return 生成的 Javabean
     */
    public static <T> T httpToBean(Class<T> cls, HttpServletRequest req, boolean hasPre)
        throws JibuException {
        if(hasPre) return httpToBean(cls,req);

        HashMap map = new HashMap();
        Enumeration names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = req.getParameter(name);
            if (!value.trim().isEmpty())
                map.put(name, value);
        }
        T bean = BeanConverter.mapToBean(cls, map);
        return bean;
    }

    /**
     * 将 HttpServletRequest 中用于分页和排序的值按照约定自动装入 Criteria 对象。
     * <p>
     * 接收的参数有： limit , start , dir , sort, sortOthers。<br>
     * 如果 limit 的值大于 0 ，分页有效。如果 sort 非 null，排序有效。
     * 如果默认的 sort 属性不能唯一标识一行，需要使用 sortOthers 属性来对其它列排序，
     * 参数的格式如 "serial_no, created_ts"。
     * @param req 要处理的 Request。
     * @return 如果没有有效的 limit 和 sort，返回 null，否则返回 Criteria 实例。
     */
    public static Criteria httpToCriteria(HttpServletRequest req) {
        Criteria criteria = new Criteria();

        if (null != req.getParameter("limit") &&
            null != req.getParameter("start")) {

            int limit = Integer.parseInt(req.getParameter("limit"));
            int start = Integer.parseInt(req.getParameter("start"));
            criteria.setLimit(limit);
            criteria.setStart(start);
        }

        criteria.setDir(req.getParameter("dir"));
        criteria.setSort(req.getParameter("sort"));

        if (criteria.getSort() != null && null != req.getParameter("sortOthers")) {
            criteria.setSort(criteria.getSort()+","+req.getParameter("sortOthers"));
        }

        if (criteria.getLimit() <=0 || criteria.getSort() == null) {
            return null;
        }
        return criteria;
    }

    /**
     * 从 Session 中得到当前用户的用户名 。
     * <p>
     * @param req HttpServletRequest。
     * @return username
     */
    public static String getUsername(HttpServletRequest req) {
        HttpSession ses = req.getSession(false);
        String username = null;
        if (null!=ses) {
            username = (String) ses.getValue("username");
        }
        return username;
    }

    /**
     * 从 Session 中得到当前用户的 Locale 。
     * <p>
     * @param req HttpServletRequest。
     * @return locale，如果session为空，返回客户端默认的Locale
     */
    public static Locale getLocale(HttpServletRequest req) {
        HttpSession ses = req.getSession(false);
        if (null!=ses) {
            Object obj = ses.getAttribute("locale");
            if( obj != null) {
                return (Locale) obj;
            }
        }
        return req.getLocale();
    }

    /**
     * 将一个字符串转化为 Locale 。
     * <p>
     * @param localeCode 用于转化为 Locale对象的字符串，格式如 zh_CN, en 等。
     * @return Locale
     */
    public static Locale convertToLocale(String localeCode) {
        Locale locale = null;
        String[] elements = localeCode.split("_");

        switch (elements.length) {
        case 1:
            locale = new Locale(elements[0]);
            break;
        case 2:
            locale = new Locale(elements[0], elements[1]);
            break;
        case 3:
            locale = new Locale(elements[0], elements[1], elements[2]);
            break;
        default:
            throw new RuntimeException("Can't handle localeCode = \"" + localeCode + "\".  Elements.length = " + elements.length);
        }
        return locale;
    }
}
