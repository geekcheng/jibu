package org.gaixie.jibu.security.interceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import javax.servlet.http.HttpServlet;

import org.gaixie.jibu.security.annotation.CIVerify;
import org.gaixie.jibu.security.interceptor.CIVerifyInterceptor;

/**
 * Security Interceptor 的 Bind 类。
 * <p>
 * 负责将 Matchers 与拦截器进行 Bind。
 */
public class SecurityInterceptorModule extends AbstractModule {

    @Override 
    protected void configure() {
        bindInterceptor(Matchers.subclassesOf(HttpServlet.class),
                        Matchers.annotatedWith(CIVerify.class),
                        new CIVerifyInterceptor());
    }
}
