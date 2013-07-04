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
import org.gaixie.jibu.security.dao.UserDAO;
import org.gaixie.jibu.security.model.Criteria;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.JibuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User 数据访问接口的 MySQL 实现。
 * <p>
 */
public class UserDAOMySQL implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOMySQL.class);
    private QueryRunner run = null;

    public UserDAOMySQL() {
	this.run = new QueryRunner();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。
     */
    public User get( Connection conn, int id) throws SQLException {
        ResultSetHandler<User> h = new BeanHandler(User.class);
        return run.query(conn
                         , "SELECT id,username,fullname,emailaddress,type,enabled FROM userbase WHERE id=? "
                         , h
                         , id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。
     */
    public User get( Connection conn, String username) throws SQLException {
        ResultSetHandler<User> h = new BeanHandler(User.class);
        return run.query(conn
                         , "SELECT id,username,fullname,emailaddress,type,enabled FROM userbase WHERE username=? "
                         , h
                         , username);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。
     */
    public User login(Connection conn,String username, String password) throws SQLException {
        ResultSetHandler<User> h = new BeanHandler(User.class);
        return run.query(conn
                         , "SELECT id,username,fullname,emailaddress,type,enabled FROM userbase WHERE username=? AND password=? and enabled=1"
                         , h
                         , username
                         , password);
    }

    public void save(Connection conn, User user) throws SQLException {
        run.update(conn
                   , "INSERT INTO userbase (fullname,username,password,emailaddress,type,enabled) VALUES (?,?,?,?,?,?)"
                   , user.getFullname()
                   , user.getUsername()
                   , user.getPassword()
                   , user.getEmailaddress()
                   , user.getType()
                   , user.getEnabled());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 除 id 属性以外，所有非空的属性将会被更新至数据库中。
     */
    public void update(Connection conn, User user) throws SQLException {
        String sql = "UPDATE userbase \n";
        Integer id = user.getId();
        user.setId(null);
        try {
            String s = SQLBuilder.beanToSQLClause(user,",");
            sql = sql + SQLBuilder.getSetClause(s) +"\n"+
                "WHERE id=? ";
        } catch (JibuException e) {
            throw new SQLException(e.getMessage());
        }
        user.setId(id);
        run.update(conn
                   , sql
                   , id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * user.getId() 不能为 null。
     */
    public void delete(Connection conn, User user) throws SQLException {
        run.update(conn
                   , "DELETE FROM  userbase WHERE id=?"
                   , user.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。
     */
    public List<User> find( Connection conn, User user) throws SQLException {
        ResultSetHandler<List<User>> h = new BeanListHandler(User.class);
        String sql = "SELECT id,username,fullname,emailaddress,type,enabled FROM userbase \n";
        try {
            String s = SQLBuilder.beanToSQLClause(user,"AND");
            sql = sql + SQLBuilder.getWhereClause(s);
        } catch (JibuException e) {
            throw new SQLException(e.getMessage());
        }
        return run.query(conn, sql, h);
    }

    public int getTotal( Connection conn, User user) throws SQLException {
        String sql = "SELECT COUNT(id) FROM userbase \n";
        try {
            String s = SQLBuilder.beanToSQLClause(user,"AND");
            sql = sql + SQLBuilder.getWhereClause(s);
        } catch (JibuException e) {
            throw new SQLException(e.getMessage());
        }
        return ((Long)run.query(conn, sql, new ScalarHandler(1))).intValue();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。
     */
    public List<User> find( Connection conn, User user, Criteria criteria) throws SQLException {
        ResultSetHandler<List<User>> h = new BeanListHandler(User.class);
        String sql = "SELECT id,username,fullname,emailaddress,type,enabled FROM userbase \n";
        try {
            String s = SQLBuilder.beanToSQLClause(user, "AND");
            sql = sql + SQLBuilder.getWhereClause(s);
            sql = SQLBuilder.getSortClause(sql,criteria);
            sql = SQLBuilder.getPagingClause(sql,criteria);
        } catch (JibuException e) {
            throw new SQLException(e.getMessage());
        }
        return run.query(conn, sql, h);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null，role.getId() 不能为 null。
     */
    public List<User> find( Connection conn, Role role) throws SQLException {
        ResultSetHandler<List<User>> h = new BeanListHandler(User.class);
        return run.query(conn
                         ,"SELECT u.id, u.username, u.fullname, u.emailaddress, u.type, u.enabled "+
                         " FROM roles AS r, user_role_map AS m, userbase AS u "+
                         " WHERE u.id = m.user_id "+
                         " AND m.role_id = r.id "+
                         " AND r.id = ? "
                         , h, role.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null，auth.getId() 不能为 null。
     */
    public List<User> find( Connection conn, Authority auth) throws SQLException {
        ResultSetHandler<List<User>> h = new BeanListHandler(User.class);
        return run.query(conn
                         ,"SELECT u.id, u.username, u.fullname, u.emailaddress, u.type, u.enabled "+
                         " FROM roles AS node, roles AS parent, user_role_map AS urm, role_authority_map AS ram, userbase AS u "+
                         " WHERE parent.id = ram.role_id "+
                         " AND node.lft BETWEEN parent.lft AND parent.rgt "+
                         " AND node.id = urm.role_id "+
                         " AND urm.user_id = u.id "+
                         " AND ram.authority_id = ? "
                         , h, auth.getId());
    }
}
