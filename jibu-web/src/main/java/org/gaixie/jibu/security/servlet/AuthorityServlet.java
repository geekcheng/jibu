package org.gaixie.jibu.security.servlet;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.annotation.CIVerify;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.utils.BundleHandler;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应权限资源相关操作的请求。
 * <p>
 */
@Singleton 
public class AuthorityServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(AuthorityServlet.class);
    
    @Inject 
    private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String rst = null;
        if ("authFind".equals(req.getParameter("ci"))) {
            rst = authFind(req,injector);
        } else if ("authAdd".equals(req.getParameter("ci"))) {
            rst = authAdd(req,injector);
        } else if ("authUpdate".equals(req.getParameter("ci"))) {
            rst = authUpdate(req,injector);
        } else if ("authDelete".equals(req.getParameter("ci"))) {
            rst = authDelete(req,injector);
        }

        PrintWriter pw = resp.getWriter();
        pw.println(rst);
        pw.close();
    }

    public void doPost(HttpServletRequest req,  HttpServletResponse resp)
        throws IOException{
        doGet(req, resp);
    }

    protected String authFind(HttpServletRequest req,
                              Injector injector) {
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Authority auth = ServletUtils.httpToBean(Authority.class,req);
            List<Authority> auths = authService.find(auth);
            for (Authority a : auths) {
                if (a.getName().length() >=5 ) {
                    a.setDisplay(rb.get(a.getName().substring(2)));
                } else {
                    a.setDisplay(a.getName());
                }
            }
            jsonMap.put("success", true);
            jsonMap.put("data", new JSONArray(auths));
        } catch (JibuException e) {
            logger.error(e.getMessage());
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String authAdd(HttpServletRequest req,
                             Injector injector) {
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Authority auth = ServletUtils.httpToBean(Authority.class,req);
            // 菜单的name 不允许前端修改，只能通过后台操作
            if(auth.getName().length() < 5) {
                authService.add(auth);
                jsonMap.put("success", true);
                jsonMap.put("message", rb.get("message.001"));
            } else {
                jsonMap.put("success", false);
                jsonMap.put("message", rb.get("auth.message.001"));
            }
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String authUpdate(HttpServletRequest req,
                                Injector injector) {
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Authority auth = ServletUtils.httpToBean(Authority.class,req);
            if(auth.getName().length() < 5) {
                authService.update(auth);
                jsonMap.put("success", true);
                jsonMap.put("message", rb.get("message.001"));
            } else {
                jsonMap.put("success", false);
                jsonMap.put("message", rb.get("auth.message.001"));
            }
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String authDelete(HttpServletRequest req,
                                Injector injector) {
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String id = req.getParameter("id");
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Authority auth = new Authority();
            auth.setId(Integer.parseInt(id));
            authService.delete(auth);
            jsonMap.put("success", true);
            jsonMap.put("message", rb.get("message.001"));
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }
}
