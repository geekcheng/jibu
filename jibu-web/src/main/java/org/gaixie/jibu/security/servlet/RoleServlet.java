package org.gaixie.jibu.security.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.annotation.CIVerify;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.service.RoleException;
import org.gaixie.jibu.security.service.RoleService;
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
 * 响应角色、权限管理相关操作的请求。
 * <p>
 */
@Singleton
public class RoleServlet extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(RoleServlet.class);

    @Inject
    private Injector injector;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String ci = req.getParameter("ci");
        String rst = null;
        if ("userBindCheck".equals(ci)) {
            rst = userBindCheck(req,injector);
        } else if ("bindUser".equals(ci)) {
            rst = bindUser(req,injector);
        } else if ("unbindUser".equals(ci)) {
            rst = unbindUser(req,injector);
        } else if ("authBindCheck".equals(ci)) {
            rst = authBindCheck(req,injector);
        } else if ("bindAuth".equals(ci)) {
            rst = bindAuth(req,injector);
        } else if ("unbindAuth".equals(ci)) {
            rst = unbindAuth(req,injector);
        } else if ("roleBindCheck".equals(ci)) {
            rst = roleBindCheck(req,injector);
        } else if ("roleAdd".equals(ci)) {
            rst = roleAdd(req,injector);
        } else if ("roleUpdate".equals(ci)) {
            rst = roleUpdate(req,injector);
        } else if ("roleDelete".equals(ci)) {
            rst = roleDelete(req,injector);
        } else if ("getAllRole".equals(ci)) {
            rst = getAllRole(req,injector);
        }

        PrintWriter pw = resp.getWriter();
        pw.println(rst);
        pw.close();
    }

    public void doPost(HttpServletRequest req,  HttpServletResponse resp)
        throws IOException{
        doGet(req, resp);
    }

    protected String userBindCheck(HttpServletRequest req,
                                   Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        RoleService roleService = injector.getInstance(RoleService.class);
        AuthorityService authService = injector.getInstance(AuthorityService.class);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String username=req.getParameter("username");

        User user = userService.get(username);
        List<Role> roles = roleService.find(user);
        List<Authority> auths = authService.find(user);
        JSONArray aryrole = new JSONArray();
        for (Role r : roles) {
            aryrole.put(r.getId());
        }

        JSONArray aryauth = new JSONArray();
        for (Authority a : auths) {
            aryauth.put(a.getId());
        }
        jsonMap.put("success", true);
        jsonMap.put("roles", aryrole);
        jsonMap.put("auths", aryauth);
        return (new JSONObject(jsonMap)).toString();
    }


    protected String bindUser(HttpServletRequest req,
                              Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        AuthorityService authService = injector.getInstance(AuthorityService.class);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String userid=req.getParameter("user.id");
        String roleid=req.getParameter("role.id");

        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            User user = new User();
            user.setId(Integer.parseInt(userid));
            Role role = new Role();
            role.setId(Integer.parseInt(roleid));

            roleService.bind(role,user);
            List<Authority> auths = authService.find(user);
            JSONArray aryauth = new JSONArray();
            for (Authority a : auths) {
                aryauth.put(a.getId());
            }

            jsonMap.put("success", true);
            jsonMap.put("auths", aryauth);
            jsonMap.put("message", rb.get("message.001"));
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String unbindUser(HttpServletRequest req,
                                Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String userid=req.getParameter("user.id");
        String roleid=req.getParameter("role.id");

        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            User user = new User();
            user.setId(Integer.parseInt(userid));
            Role role = new Role();
            role.setId(Integer.parseInt(roleid));

            roleService.unbind(role,user);
            List<Authority> auths = authService.find(user);
            JSONArray aryauth = new JSONArray();
            for (Authority a : auths) {
                aryauth.put(a.getId());
            }

            jsonMap.put("success", true);
            jsonMap.put("auths", aryauth);
            jsonMap.put("message", rb.get("message.001"));
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String unbindAuth(HttpServletRequest req,
                                Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        UserService userService = injector.getInstance(UserService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String authid=req.getParameter("authority.id");
        String roleid=req.getParameter("role.id");

        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Authority auth = new Authority();
            auth.setId(Integer.parseInt(authid));
            Role role = new Role();
            role.setId(Integer.parseInt(roleid));

            roleService.unbind(role,auth);
            List<User> users = userService.find(auth);
            JSONArray aryuser = new JSONArray();
            for (User u : users) {
                aryuser.put(u.getId());
            }

            jsonMap.put("success", true);
            jsonMap.put("users", aryuser);
            jsonMap.put("message", rb.get("message.001"));
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String authBindCheck(HttpServletRequest req,
                                   Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        RoleService roleService = injector.getInstance(RoleService.class);
        AuthorityService authService = injector.getInstance(AuthorityService.class);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String id=req.getParameter("id");
        Authority auth = authService.get(Integer.parseInt(id));
        List<Role> roles = roleService.find(auth);
        List<User> users = userService.find(auth);

        JSONArray aryrole = new JSONArray();
        for (Role r : roles) {
            aryrole.put(r.getId());
        }

        JSONArray aryuser = new JSONArray();
        for (User u : users) {
            aryuser.put(u.getId());
        }

        jsonMap.put("success", true);
        jsonMap.put("roles", aryrole);
        jsonMap.put("users", aryuser);
        return (new JSONObject(jsonMap)).toString();
    }

    protected String bindAuth(HttpServletRequest req,
                              Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        RoleService roleService = injector.getInstance(RoleService.class);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String authid=req.getParameter("authority.id");
        String roleid=req.getParameter("role.id");

        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));
        try {
            Authority auth = new Authority();
            auth.setId(Integer.parseInt(authid));
            Role role = new Role();
            role.setId(Integer.parseInt(roleid));

            roleService.bind(role,auth);

            List<User> users = userService.find(auth);
            JSONArray aryuser = new JSONArray();
            for (User u : users) {
                aryuser.put(u.getId());
            }

            jsonMap.put("success", true);
            jsonMap.put("users", aryuser);
            jsonMap.put("message", rb.get("message.001"));
        } catch (JibuException e) {
            logger.error(e.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String roleBindCheck(HttpServletRequest req,
                                   Injector injector) {
        UserService userService = injector.getInstance(UserService.class);
        RoleService roleService = injector.getInstance(RoleService.class);
        AuthorityService authService = injector.getInstance(AuthorityService.class);

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String id = req.getParameter("id");
        Role role = roleService.get(Integer.parseInt(id));
        List<User> users = userService.find(role);
        List<Authority> auths = authService.find(role);
        JSONArray aryuser = new JSONArray();
        for (User u : users) {
            aryuser.put(u.getId());
        }

        JSONArray aryauth = new JSONArray();
        for (Authority a : auths) {
            aryauth.put(a.getId());
        }

        jsonMap.put("success", true);
        jsonMap.put("users", aryuser);
        jsonMap.put("auths", aryauth);
        return (new JSONObject(jsonMap)).toString();
    }


    protected String roleAdd(HttpServletRequest req,
                             Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Role role = ServletUtils.httpToBean(Role.class,req);
            String pid = req.getParameter("pid");
            Role prole = roleService.get(Integer.parseInt(pid));
            roleService.add(role,prole);
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

    protected String roleUpdate(HttpServletRequest req,
                                Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Role role = ServletUtils.httpToBean(Role.class,req);
            roleService.update(role);
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

    protected String roleDelete(HttpServletRequest req,
                                Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        String id = req.getParameter("id");
        BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));

        try {
            Role role = new Role();
            role.setId(Integer.parseInt(id));
            roleService.delete(role);
            jsonMap.put("success", true);
            jsonMap.put("message", rb.get("message.001"));
        } catch (RoleException e) {
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("role.message.001"));
        } catch (JibuException je) {
            logger.error(je.getMessage());
            jsonMap.put("success", false);
            jsonMap.put("message", rb.get("message.002"));
        } finally {
            return (new JSONObject(jsonMap)).toString();
        }
    }

    protected String getAllRole(HttpServletRequest req,
                                Injector injector) {
        RoleService roleService = injector.getInstance(RoleService.class);
        StringBuilder sb = new StringBuilder();
        List<Role> roles = roleService.getAll();
        sb.append("[");
        /*
         *    pre:  前一个节点的层数
         *    cur:  当前节点的层数
         *    dist: 上一个节点和当前节点的层距
         */
        int pre=-1;
        for(Role role : roles) {
            int cur = role.getDepth();
            int dist = pre-cur;
            String node = "{id:"+role.getId()+",text:\""+role.getName()
                +"\",qtip:\""+role.getDescription()+"\",checked:false,leaf:"
                +(((role.getRgt()-role.getLft())>1) ? "false" : "true}");
            if (dist < 0) {                         // 层数在增加，根节点的depth 为0，所以 dist = -1-0 =-1
                if (cur==0) sb.append(node);        // 根
                else sb.append(",children:["+node);
            } else if (dist >0) {                   // 层数在减少
                for (int i=0;i<dist;i++) {          // 减少了几层，就补几个个 "]}"
                    sb.append("]}");
                }
                sb.append(","+node);
            } else {                                // 层数没变化
                sb.append(","+node);
            }
            pre = cur;
        }

        for (int i=0;i<pre;i++) {                  // 最后一个节点的层也需要部 "]}"
            sb.append("]}");
        }
        sb.append("]");
        return sb.toString();
    }
}
