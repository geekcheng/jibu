package org.gaixie.jibu.security.servlet;

import org.gaixie.jibu.config.JibuConfig;
import org.gaixie.jibu.security.dao.SecurityDAOModule;
import org.gaixie.jibu.security.interceptor.SecurityInterceptorModule;
import org.gaixie.jibu.security.service.SecurityServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * GuiceServletConfig 是一个 Listener 类，在应用 deploy 时被触发。
 * <p>
 * 这个类应该在 web.xml 中配置，如：
 *
 * <pre>
 * {@code
 * <listener>
 *   <listener-class>org.gaixie.jibu.security.servlet.GuiceServletConfig</listener-class>
 * </listener>
 * }
 * </pre>
 *
 * 由于不同的应用，需要 Guice 注入的内容不同，所以应该有自己的 GuiceServletConfig。
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    /**
     * 得到一个 Injector。
     * <p>
     * 将所有 Jibu 中需要 Guice 注入的类初始化到 Injector。
     *
     * @return 实例化的 Injector
     */
    @Override
    protected Injector getInjector() {
        String databaseType = JibuConfig.getProperty("databaseType");
        return Guice.createInjector(new SecurityServletModule(),
                                    new SecurityInterceptorModule(),
                                    new SecurityServiceModule(),new SecurityDAOModule(databaseType));
    }
}