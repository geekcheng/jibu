package org.gaixie.jibu.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.servlet.ServletUtils;
import org.gaixie.jibu.utils.BundleHandler;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * LoginFilter 对所有请求进行拦截并验证是否登录。
 * <p>
 * 对于特定的 URL 请求进行拦截，判断 HttpSession 中是否有 username。 如果没有，直接以 JSON 格式返回提示信息。
 */
@Singleton
public class LoginFilter implements Filter {

    @Inject private Injector injector;

    /**
     * The filter configuration object we are associated with. If this value is
     * null, this filter instance is not currently configured.
     */
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        HttpSession ses = ((HttpServletRequest) req).getSession(false);
        boolean allowedRequest = false;

        if (null != ses) {
            if (null != ses.getAttribute("username")) {
                allowedRequest = true;
            }
        }

        /*
         * 如果登录验证通过，继续判断是否一个受控的 servlet
         * 如果已 .z 的后缀结尾，表示请求的 Servlet 受控，需要进行权限验证
         * 此验证策略要求每个菜单模块都对应一个独立的 servlet，并且以下面的格式初始化到表中
         * insert into authorities (name,value) values ('xx.xxx','xxxxx.z');
         * xx.xxx是菜单项， xxxxx.z 是受控的 Servlet
         */ 
        if (allowedRequest) {
            String value = ((HttpServletRequest)req).getServletPath();
            if(value.endsWith(".z")){
                AuthorityService auth = 
                    injector.getInstance(AuthorityService.class);
                allowedRequest = auth.verify(value
                                             ,(String)ses.getAttribute("username"));
                if (!allowedRequest) {
                    BundleHandler rb = 
                        new BundleHandler(ServletUtils.getLocale((HttpServletRequest)req));
                    res.setContentType("application/json;charset=UTF-8");
                    PrintWriter pw = res.getWriter();
                    pw.println("{\"message\":\""+rb.get("message.003")+"\",\"success\":false}");
                    pw.close();
                    return;
                }
            }
        }

        if (!allowedRequest) {
            // 加入ajax请求判断，所有的ajax请求都会带有ci参数，非ajax不带ci参数
            if (req.getParameter("ci") != null) {
                res.setContentType("application/json;charset=UTF-8");
                PrintWriter pw = res.getWriter();
                pw.println("{success:false,data:[],message:'expired'}");
                pw.close();
            } else {
                if (((HttpServletRequest) req).getRequestURI()
                        .indexOf("Main.y") != -1) {
                    ((HttpServletResponse) res)
                            .sendRedirect("Login.x?ci=logout&reason=sessionExpired");
                } else {
                    ((HttpServletResponse) res).sendRedirect("Login.x");
                }
            }
            return;
        }

        // 不要cache 通过此Filter的response。
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
        ((HttpServletResponse) res).setHeader("Pragma", "no-cache");
        ((HttpServletResponse) res).setHeader("Cache-Control",
                "no-cache,no-store,max-age=0");
        chain.doFilter(req, res);
    }

    public void destroy() {
        this.filterConfig = null;
    }
}
