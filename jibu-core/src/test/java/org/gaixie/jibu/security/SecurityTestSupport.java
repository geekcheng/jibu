package org.gaixie.jibu.security;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.gaixie.jibu.config.JibuConfig;
import org.gaixie.jibu.security.dao.SecurityDAOModule;
import org.gaixie.jibu.security.service.SecurityServiceModule;
import org.gaixie.jibu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;


/**
 * 需要Guice注入的测试类的父类，使测试类取得injector，通过同步化保证injector只被创建一
 * 次，同时通过读取jibu.properties文件的databaseType，使DAO的测试类可以无须修改就
 * 测试多种类型的数据库。
 */
public class SecurityTestSupport {
    private static Injector injector;

    public synchronized Injector getInjector() {
        if(injector != null){
            return injector;
        }
        String databaseType = JibuConfig.getProperty("databaseType");
        injector = Guice.createInjector(new SecurityServiceModule(),
                                        new SecurityDAOModule(databaseType));
        return injector;
    }

    /**
     * 测试用例运行前需要清空所有表的数据。
     * 清表的顺序参考：
     * http://10.10.1.10/docs/jibu-schema/v2.0.0/deletionOrder.txt
     */
    protected void clearTable() {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            QueryRunner run = new QueryRunner();
            run.update(conn, "DELETE from user_setting_map"); 
            run.update(conn, "DELETE from user_role_map"); 
            run.update(conn, "DELETE from role_authority_map"); 
            run.update(conn, "DELETE from tokens"); 
            run.update(conn, "DELETE from settings"); 
            run.update(conn, "DELETE from authorities "); 
            run.update(conn, "DELETE from roles"); 
            run.update(conn, "DELETE from userbase");
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            System.out.println(e.getMessage());
        }
    }
}
