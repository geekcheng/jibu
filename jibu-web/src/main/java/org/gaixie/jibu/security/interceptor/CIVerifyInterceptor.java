package org.gaixie.jibu.security.interceptor;

import com.google.inject.Injector;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.servlet.ServletUtils;
import org.gaixie.jibu.utils.BundleHandler;

public class CIVerifyInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        Object []args = inv.getArguments();
        Object obj = null;
        HttpServletRequest req = (HttpServletRequest)args[0];
        Injector injector = (Injector)args[1];
        String value = req.getServletPath()+"?ci="+req.getParameter("ci");
        String username = ServletUtils.getUsername(req);
        AuthorityService authService = injector.getInstance(AuthorityService.class);
        boolean allowed = authService.verify(value,username);
        if (allowed) {
            obj = inv.proceed();
        } else {
            BundleHandler rb = new BundleHandler(ServletUtils.getLocale(req));
            obj = "{\"message\":\""+rb.get("message.003")+"\",\"success\":false}";
        }
        return obj;
    }
}
