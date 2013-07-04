package org.gaixie.jibu.security.service;

import com.google.inject.AbstractModule;

import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.service.LoginService;
import org.gaixie.jibu.security.service.MonitorService;
import org.gaixie.jibu.security.service.RoleService;
import org.gaixie.jibu.security.service.SettingService;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.security.service.impl.AuthorityServiceImpl;
import org.gaixie.jibu.security.service.impl.LoginServiceImpl;
import org.gaixie.jibu.security.service.impl.MonitorServiceImpl;
import org.gaixie.jibu.security.service.impl.RoleServiceImpl;
import org.gaixie.jibu.security.service.impl.SettingServiceImpl;
import org.gaixie.jibu.security.service.impl.UserServiceImpl;

/**
 * Security 服务层的 Bind 类。
 * <p>
 */
public class SecurityServiceModule extends AbstractModule {

    @Override protected void configure() {
        bind(LoginService.class).to(LoginServiceImpl.class);
        bind(MonitorService.class).to(MonitorServiceImpl.class);
        bind(UserService.class).to(UserServiceImpl.class);
        bind(RoleService.class).to(RoleServiceImpl.class);
        bind(SettingService.class).to(SettingServiceImpl.class);
        bind(AuthorityService.class).to(AuthorityServiceImpl.class);
    }
}
