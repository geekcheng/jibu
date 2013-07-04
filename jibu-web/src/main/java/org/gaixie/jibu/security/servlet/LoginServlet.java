package org.gaixie.jibu.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gaixie.jibu.mail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Token;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.LoginException;
import org.gaixie.jibu.security.service.LoginService;
import org.gaixie.jibu.security.service.TokenException;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.BundleHandler;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * 响应登录请求的 Servlet，不被任何 Filter拦截。
 * <p>
 */
@Singleton
public class LoginServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    @Inject
    private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        if ("login".equals(req.getParameter("ci"))) {
            login(injector, req, resp);
        } else if ("loginAjax".equals(req.getParameter("ci"))) {
            loginAjax(injector, req, resp);
        } else if ("logout".equals(req.getParameter("ci"))) {
            logout(req, resp);
        } else if ("getToken".equals(req.getParameter("ci"))) {
            getToken(injector, req, resp);
        } else if ("resetPassword".equals(req.getParameter("ci"))) {
            resetPassword(injector, req, resp);
        } else {
            loadPage(req, resp, null);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        doGet(req, resp);
    }

    protected void login(Injector injector, HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {
        LoginService loginService = injector.getInstance(LoginService.class);
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            loginService.login(username, password);
            // check if we have a session
            HttpSession ses = req.getSession(true);
            ses.setAttribute("username", username);
            resp.sendRedirect("Main.y");
        } catch (LoginException le) {
            loadPage(req, resp, "login.message.001");
        }
    }

    protected void loginAjax(Injector injector, HttpServletRequest req,
                             HttpServletResponse resp) throws IOException {
        LoginService loginService = injector.getInstance(LoginService.class);
        resp.setContentType("application/json;charset=UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));
        PrintWriter pw = resp.getWriter();
        try {
            loginService.login(username, password);
            // check if we have a session
            HttpSession ses = req.getSession(true);
            ses.setAttribute("username", username);
            pw.println("{\"success\":true,message:'" + rb.get("message.001")
                       + "'}");
        } catch (LoginException le) {
            pw.println("{\"success\":false,message:'"
                       + rb.get("login.message.001") + "'}");
        }
        pw.close();
    }

    protected void logout(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        // check if we have a session
        HttpSession ses = req.getSession(false);
        if (ses != null) {
            ses.invalidate();
        }

        String reason = req.getParameter("reason");
        if ("sessionExpired".equals(reason)) {
            loadPage(req, resp, "login.message.010");
        } else if ("serviceReloaded".equals(reason)) {
            loadPage(req, resp, "login.message.011");
        } else {
            resp.sendRedirect("Login.x");
        }
    }

    protected void loadPage(HttpServletRequest req, HttpServletResponse resp,
                            String message) throws IOException {
        HttpSession ses = req.getSession(false);
        if (ses != null) {
            String username = (String) ses.getAttribute("username");
            if (username != null) {
                resp.sendRedirect("Main.y");
                return;
            }
        }

        resp.setContentType("text/html;charset=UTF-8");
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"login\">");
        sb.append("<h1><a title=\"Powered by Gaixie.ORG\" href=\"http://gaixie.org/\">Gaixie.ORG</a></h1>\n");
        if (message != null) {
            sb.append("  <div id=\"login_error\">");
            sb.append(rb.get(message));
            sb.append("  </div>");
        }
        sb.append("  <form id=\"login_form\" method=\"post\" action=\"Login.x?ci=login\">\n"
                  + "    <p>\n"
                  + "      <label>"
                  + rb.get("login.username")
                  + "<br>\n"
                  + "      <input id=\"user_name\" type=\"text\" value=\"\" name=\"username\"/>\n"
                  + "      </label>\n"
                  + "    </p>\n"
                  + "    <p>\n"
                  + "      <label>"
                  + rb.get("login.password")
                  + "<br>\n"
                  + "      <input id=\"user_pass\" type=\"password\" value=\"\" name=\"password\" autocomplete=\"off\"/>\n"
                  + "      </label>\n"
                  + "    </p>\n"
                  + "    <input id=\"login_button\" type=\"submit\" value=\""
                  + rb.get("button.login")
                  + "\" />\n"
                  + "    <a id=\"lostpw\" href=\"Login.x?ci=getToken\">"
                  + rb.get("login.lostpassword") + "</a>\n" + "  </form>\n");
        sb.append("</div>");

        PrintWriter pw = resp.getWriter();
        pw.println(ServletUtils.head(rb.get("login.title")) + loginCSS()
                   + ServletUtils.favicon() + ServletUtils.body() + sb.toString() + ServletUtils.footer()
                   + ServletUtils.closeHtml());
        pw.close();
    }

    private void lost(HttpServletRequest req, HttpServletResponse resp,
                      String message, boolean showForm) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"login\">");
        sb.append("<h1><a title=\"Powered by Gaixie\" href=\"http://gaixie.org/\">Gaixie</a></h1>\n");
        sb.append(message);
        // 如果操作成功，不再显示 from，失败才显示。
        if (showForm) {
            sb.append("  <form id=\"login_form\" method=\"post\" action=\"Login.x?ci=getToken\">\n"
                      + "    <p>\n"
                      + "      <label>"
                      + rb.get("login.username")
                      + "<br>\n"
                      + "      <input id=\"user_name\" type=\"text\" value=\"\" name=\"username\"/>\n"
                      + "      </label>\n"
                      + "    </p>\n"
                      + "    <input id=\"login_button\" type=\"submit\" value=\""
                      + rb.get("button.submit") + "\" />\n" + "  </form>\n");
        } else {
            sb.append("<p id=\"backto\"><a href=\"Login.x\">"
                      + rb.get("login.backtologin") + "</a></p>\n");
        }
        sb.append("</div>");

        PrintWriter pw = resp.getWriter();
        pw.println(ServletUtils.head(rb.get("login.reset.step1.title"))
                   + ServletUtils.favicon() + loginCSS() + ServletUtils.body() + sb.toString()
                   + ServletUtils.footer() + ServletUtils.closeHtml());
        pw.close();
    }

    protected void getToken(Injector injector, HttpServletRequest req,
                            HttpServletResponse resp) throws IOException {
        LoginService loginService = injector.getInstance(LoginService.class);
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));
        UserService userService = injector.getInstance(UserService.class);

        String username = req.getParameter("username");
        // 初次进入getToken页面
        if (username == null) {
            lost(req, resp,
                 "<div id=\"login_message\">" + rb.get("login.message.002")
                 + "</div>\n", true);
            return;
        }

        User user = userService.get(username);
        if (user != null) {
            Token token = loginService.generateToken(username);
            // 成功产生 token
            if (token != null) {
                String tokenUrl = req.getRequestURL()
                    + "?ci=resetPassword&key=" + token.getValue();
                JavaMailSender sender = new JavaMailSender();
                String text = rb.get("login.lostpassword.mail.tpl");
                Object[] args = { user.getFullname(), user.getUsername(),
                                  tokenUrl };
                sender.sendEmail("noreply@gaixie.org",
                                 user.getEmailaddress(), "Reset Password Assistance",
                                 MessageFormat.format(text, args));
                lost(req,
                     resp,
                     "<div id=\"login_message\">"
                     + rb.get("login.message.003") + "</div>\n",
                     false);
                return;

            }
        }
        // 产生 token 失败
        lost(req, resp,
             "<div id=\"login_error\">" + rb.get("login.message.004")
             + "</div>\n", true);
    }

    protected void resetPassword(Injector injector, HttpServletRequest req,
                                 HttpServletResponse resp) throws IOException {
        LoginService loginService = injector.getInstance(LoginService.class);
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        String key = req.getParameter("key");
        String password = req.getParameter("password");
        String repassword = req.getParameter("repassword");

        if (password == null) {
            reset(req, resp,
                  "<div id=\"login_message\">" + rb.get("login.message.005")
                  + "</div>\n", true);
        } else if (!password.equals(repassword)) {
            reset(req, resp,
                  "<div id=\"login_error\">" + rb.get("login.message.006")
                  + "</div>\n", true);
        } else {
            try {
                loginService.resetPassword(key, password);
                reset(req,
                      resp,
                      "<div id=\"login_message\">"
                      + rb.get("login.message.007") + "</div>\n",
                      false);
            } catch (TokenException te) {
                reset(req,
                      resp,
                      "<div id=\"login_error\">"
                      + rb.get("login.message.008") + "</div>\n",
                      true);
            } catch (JibuException e) {
                System.out.println(e.getMessage());
                reset(req,
                      resp,
                      "<div id=\"login_error\">"
                      + rb.get("login.message.009") + "</div>\n",
                      true);
            }
        }
    }

    private void reset(HttpServletRequest req, HttpServletResponse resp,
                       String message, boolean showForm) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"login\">");
        sb.append("<h1><a title=\"Powered by Gaixie\" href=\"http://gaixie.org/\">Gaixie</a></h1>\n");
        sb.append(message);
        // 如果操作成功，不再显示 from，失败才显示。
        if (showForm) {
            sb.append("  <form id=\"login_form\" method=\"post\" action=\"Login.x?ci=resetPassword\">\n"
                      + "    <input type=\"hidden\" name=\"key\" value=\""
                      + req.getParameter("key")
                      + "\">\n"
                      + "    <p>\n"
                      + "      <label>"
                      + rb.get("login.password")
                      + "<br>\n"
                      + "      <input id=\"user_name\" type=\"password\" value=\"\" name=\"password\"/>\n"
                      + "      </label>\n"
                      + "    </p>\n"
                      + "    <p>\n"
                      + "      <label>"
                      + rb.get("login.repassword")
                      + "<br>\n"
                      + "      <input id=\"user_pass\" type=\"password\" value=\"\" name=\"repassword\"/>\n"
                      + "      </label>\n"
                      + "    </p>\n"
                      + "    <input id=\"login_button\" type=\"submit\" value=\""
                      + rb.get("button.submit") + "\" />\n" + "  </form>\n");
        } else {
            sb.append("<p id=\"backto\"><a href=\"Login.x\">"
                      + rb.get("login.backtologin") + "</a></p>\n");
        }
        sb.append("</div>");

        PrintWriter pw = resp.getWriter();
        pw.println(ServletUtils.head(rb.get("login.reset.step2.title"))
                   + ServletUtils.favicon() + loginCSS() + ServletUtils.body() + sb.toString()
                   + ServletUtils.footer() + ServletUtils.closeHtml());
        pw.close();
    }

    private String loginCSS() {
        StringBuilder sb = new StringBuilder();
        sb.append("<style type=\"text/css\">");

        sb.append("* {margin:0;padding:0;}\n");
        sb.append("#login {margin:7em auto;width:300px;}\n");
        sb.append("h1 a {background: url(\"images/logo-login.png\") no-repeat scroll center top transparent;"
                  +"display: block;height: 40px;overflow: hidden;padding-bottom: 12px;text-indent: -9999px;width: 300px;}\n");
        sb.append("#login_error {background-color:#FFEBE8;border-color:#CC0000;"
                  + "border-style:solid;border-width:1px;margin:0 0 16px 0;padding:10px 16px;font-size:12px;}\n");
        sb.append("#login_message {background-color:#FFFFE0;border-color:#E6DB55;"
                  + "border-style:solid;border-width:1px;margin:0 0 16px 0;padding:10px 16px;font-size:12px;}\n");
        sb.append("#login_form {-moz-box-shadow:rgba(200,200,200,0.7) 0 4px 18px;-webkit-box-shadow:rgba(200,200,200,0.7) 0 4px 18px;"
                  +"-khtml-box-shadow:rgba(200,200,200,0.7) 0 4px 18px;box-shadow:rgba(200,200,200,0.7) 0 4px 18px;"
                  +"background:#FFFFFF none repeat scroll 0 0;border:1px solid #CCCCCC;font-weight:normal;"
                  +"-moz-border-radius:3px;-khtml-border-radius:3px;-webkit-border-radius:3px;margin-bottom:0;padding:20px 26px;}\n");
        sb.append("label {font-size:12px;}\n");
        sb.append("#user_pass, #user_name {background:none repeat scroll 0 0 #FBFBFB;border:1px solid #CCCCCC;font-size:15px;margin-bottom:12px;"
                  + "margin-right:6px;margin-top:2px;padding:3px;width:97%;}\n");
        sb.append("#login_button {font-size:12px;padding-left:12px;padding-right:12px;padding-top:2px;width:auto;}\n");
        sb.append("#lostpw {font-size:12px;}\n");
        sb.append("#backto {font-size:12px;margin:0 0 0 8px;padding:0px 16px;}");
        sb.append("</style>");
        return sb.toString();
    }
}
