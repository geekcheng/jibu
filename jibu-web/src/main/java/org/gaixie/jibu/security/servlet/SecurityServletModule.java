package org.gaixie.jibu.security.servlet;

import com.google.inject.servlet.ServletModule;

/**
 * Security Servlet 的 Bind 类。
 * <p>
 * Servlet mapping ，以及 Filter的拦截策略都在这里定义。
 */
public class SecurityServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("*.y", "*.z").through(LoginFilter.class);
        serve("/").with(LoginServlet.class);
        serve("/Login.x").with(LoginServlet.class);
        serve("/Main.y").with(MainServlet.class);
        serve("/Setting.y").with(SettingServlet.class);
        serve("/User.z").with(UserServlet.class);
        serve("/Authority.z").with(AuthorityServlet.class);
        serve("/Role.z").with(RoleServlet.class);
        serve("/Monitor.z").with(MonitorServlet.class);
    }
}