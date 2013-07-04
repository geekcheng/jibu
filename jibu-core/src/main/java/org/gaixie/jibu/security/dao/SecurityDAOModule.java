package org.gaixie.jibu.security.dao;

import com.google.inject.AbstractModule;

import org.gaixie.jibu.security.dao.AuthorityDAO;
import org.gaixie.jibu.security.dao.RoleDAO;
import org.gaixie.jibu.security.dao.SettingDAO;
import org.gaixie.jibu.security.dao.TokenDAO;
import org.gaixie.jibu.security.dao.UserDAO;
import org.gaixie.jibu.security.dao.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security 数据访问层的 Bind 类，根据 jibu.properties 中的 databaseType
 * 将 数据访问接口与实现进行绑定。
 * <p>
 * 默认为 PostgreSQL，需要手动创建数据库及表
 */
public class SecurityDAOModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(SecurityDAOModule.class);
    private final String databaseType;

    public SecurityDAOModule() {
        this("PostgreSQL");
    }

    public SecurityDAOModule(String databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    protected void configure() {
        if ("PostgreSQL".equalsIgnoreCase(databaseType)){
            bind(UserDAO.class).to(UserDAOPgSQL.class);
            bind(AuthorityDAO.class).to(AuthorityDAOPgSQL.class);
            bind(RoleDAO.class).to(RoleDAOPgSQL.class);
            bind(SettingDAO.class).to(SettingDAOPgSQL.class);
            bind(TokenDAO.class).to(TokenDAOPgSQL.class);
        }
        if ("MySQL".equalsIgnoreCase(databaseType)){
            bind(UserDAO.class).to(UserDAOMySQL.class);
            bind(AuthorityDAO.class).to(AuthorityDAOMySQL.class);
            bind(RoleDAO.class).to(RoleDAOMySQL.class);
            bind(SettingDAO.class).to(SettingDAOMySQL.class);
            bind(TokenDAO.class).to(TokenDAOMySQL.class);
        }
    }
}
