package org.gaixie.jibu.security.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.annotation.CIVerify;
import org.gaixie.jibu.security.model.Criteria;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.BundleHandler;

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
 * 响应用户管理相关操作的请求。
 * <p>
 */
@Singleton
public class UserServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(UserServlet.class);

    @Inject
    private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String rst = null;
        if ("userAdd".equals(req.getParameter("ci"))) {
            rst = userAdd(req,injector);
        } else if ("userUpdate".equals(req.getParameter("ci"))) {
            rst = userUpdate(req,injector);
        } else if ("userDelete".equals(req.getParameter("ci"))) {
            rst = userDelete(req,injector);
        } else if ("userFind".equals(req.getParameter("ci"))) {
            rst = userFind(req,injector);
        }

        PrintWriter pw = resp.getWriter();
        pw.println(rst);
        pw.close();
    }

    public void doPost(HttpServletRequest req,  HttpServletResponse resp)
        throws IOException{
        doGet(req, resp);
    }

    protected String userAdd(HttpServletRequest req,
                             Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            User user = ServletUtils.httpToBean(User.class,req);
            userService.add(user);
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

    protected String userUpdate(HttpServletRequest req,
                                Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            User user = ServletUtils.httpToBean(User.class,req);
            userService.update(user);
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

    protected String userDelete(HttpServletRequest req,
                                Injector injector) {
        UserService userService = injector.getInstance(UserService.class);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        String id = req.getParameter("id");
        try {
            User user = new User();
            user.setId(Integer.parseInt(id));
            userService.delete(user);
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

    protected String userFind(HttpServletRequest req,
                              Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));
        try {
            User user = ServletUtils.httpToBean(User.class,req);
            Criteria criteria = ServletUtils.httpToCriteria(req);
            List<User> users = userService.find(user,criteria);
            jsonMap.put("success", true);
            jsonMap.put("data", new JSONArray(users));
            if (null != criteria && criteria.getLimit()>0) {
                jsonMap.put("total", criteria.getTotal());
            }
        } catch (JibuException e) {
            logger.error(e.getMessage());
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }
}
