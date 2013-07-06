package org.gaixie.jibu.security.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.gaixie.jibu.utils.SQLBuilder;
import org.gaixie.jibu.security.dao.SettingDAO;
import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.JibuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Setting 数据访问接口的 PostgreSQL 实现。
 * <p>
 */
public class SettingDAOPgSQL implements SettingDAO {
    private static final Logger logger = LoggerFactory.getLogger(SettingDAOPgSQL.class);
    private QueryRunner run = null;

    public SettingDAOPgSQL() {
        this.run = new QueryRunner();
    }

    public Setting get(Connection conn, int id) throws SQLException {
        ResultSetHandler<Setting> h = new BeanHandler(Setting.class);
        return run.query(conn
                         , "SELECT id,name,value,sortindex,enabled FROM settings WHERE id=? "
                         , h
                         , id);
    }

    public Setting get(Connection conn, String name, String value) throws SQLException {
        ResultSetHandler<Setting> h = new BeanHandler(Setting.class);
        return run.query(conn
                         , "SELECT id,name,value,sortindex,enabled FROM settings WHERE name=? AND value=? "
                         , h
                         , name
                         , value);
    }

    public Setting getDefault(Connection conn, String name) throws SQLException {
        ResultSetHandler<Setting> h = new BeanHandler(Setting.class);
        return run.query(conn
                         , "SELECT id,name,value,sortindex,enabled FROM settings WHERE sortindex = 0 and name=? "
                         , h
                         , name);
    }

    public Setting getByUsername(Connection conn, String name, String username) throws SQLException {
        ResultSetHandler<Setting> h = new BeanHandler(Setting.class);
        String sql =
            " SELECT s.id,s.name,s.value,s.sortindex,s.enabled \n"+
            " FROM settings s, userbase u, user_setting_map usm \n"+
            " WHERE u.username=? \n"+
            "       AND u.id = usm.user_id \n"+
            "       AND usm.setting_id = s.id \n"+
            "       AND s.name = ?";

        return run.query(conn,sql
                         , h
                         , username, name);
    }

    public void save(Connection conn, Setting setting) throws SQLException {
        run.update(conn
                   , "INSERT INTO settings (name,value,sortindex,enabled) VALUES (?,?,?,?)"
                   , setting.getName()
                   , setting.getValue()
                   , setting.getSortindex()
                   , setting.getEnabled());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 除 id 属性以外，所有非空的属性将会被更新至数据库中。
     */
    public void update(Connection conn, Setting setting) throws SQLException {
        String sql = "UPDATE settings \n";
        Integer id = setting.getId();
        setting.setId(null);
        try {
            String s = SQLBuilder.beanToSQLClause(setting,",");
            sql = sql + SQLBuilder.getSetClause(s) +"\n"+
                "WHERE id=? ";
        } catch (JibuException e) {
            throw new SQLException(e.getMessage());
        }
        run.update(conn
                   , sql
                   , id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * setting.getId() 不能为 null。
     */
    public void delete(Connection conn, Setting setting) throws SQLException {
        run.update(conn
                   , "DELETE FROM settings WHERE id=?"
                   , setting.getId());
    }

    public List<Setting> findByName(Connection conn, String name) throws SQLException {
        ResultSetHandler<List<Setting>> h = new BeanListHandler(Setting.class);
        return run.query(conn
                         ,"SELECT id,name,value,sortindex,enabled FROM settings WHERE name =? ORDER BY sortindex"
                         ,h,name);
    }

    public List<Setting> getAllDefault(Connection conn) throws SQLException {
        ResultSetHandler<List<Setting>> h = new BeanListHandler(Setting.class);
        return run.query(conn
                         ,"SELECT id,name,value,sortindex,enabled FROM settings WHERE sortindex =0"
                         ,h);
    }

    public List<Setting> findByUsername(Connection conn, String username) throws SQLException {
        ResultSetHandler<List<Setting>> h = new BeanListHandler(Setting.class);
        String sql =
            " SELECT s.id,s.name,s.value,s.sortindex,s.enabled \n"+
            " FROM settings s, userbase u, user_setting_map usm \n"+
            " WHERE u.username=? \n"+
            "       AND u.id = usm.user_id \n"+
            "       AND usm.setting_id = s.id \n";

        return run.query(conn,sql
                         , h
                         , username);
    }

    public void bind(Connection conn, int sid, int uid) throws SQLException {
        run.update(conn
                   , "INSERT INTO user_setting_map (setting_id,user_id) values (?,?)"
                   , sid
                   , uid);
    }

    public void unbind(Connection conn, int sid, int uid) throws SQLException {
        run.update(conn
                   , "DELETE FROM user_setting_map WHERE setting_id = ? AND user_id = ?"
                   , sid
                   , uid);
    }

    public void unbindAll(Connection conn, int uid) throws SQLException {
        run.update(conn
                   , "DELETE FROM user_setting_map WHERE user_id = ?"
                   , uid);
    }
}
