package org.gaixie.jibu.security.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.utils.SQLBuilder;
import org.gaixie.jibu.security.dao.AuthorityDAO;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * Authority 数据访问接口的 PostgreSQL 实现。
 * <p>
 */
public class AuthorityDAOPgSQL implements AuthorityDAO {
    private QueryRunner run = null;

    public AuthorityDAOPgSQL() {
	this.run = new QueryRunner();
    }

    public Authority get( Connection conn, int id) throws SQLException {
        ResultSetHandler<Authority> h = new BeanHandler(Authority.class);
        return run.query(conn
                         , "SELECT id,name,value FROM authorities WHERE id=? "
                         , h
                         , id);
    }

    public Authority get( Connection conn, String value) throws SQLException {
        ResultSetHandler<Authority> h = new BeanHandler(Authority.class);
        return run.query(conn
                         , "SELECT id,name,value FROM authorities WHERE value=? "
                         , h
                         , value);
    }

    public void save(Connection conn, Authority auth) throws SQLException {
        run.update(conn
                   , "INSERT INTO authorities (name,value) values (?,?)"
                   , auth.getName()
                   , auth.getValue());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 除 id 属性以外，所有非空的属性将会被更新至数据库中。
     */
    public void update(Connection conn, Authority auth) throws SQLException {
        String sql = "UPDATE authorities \n";
        Integer id = auth.getId();
        auth.setId(null);
        try {
            String s = SQLBuilder.beanToSQLClause(auth,",");
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
     * 按 name 排序。
     */
    public List<Authority> getAll( Connection conn) throws SQLException {
        ResultSetHandler<List<Authority>> h = new BeanListHandler(Authority.class);
        return run.query(conn
                         , "SELECT id,name,value FROM authorities order by name "
                         , h);
    }

    /**
     * {@inheritDoc}
     * <p>
     * auth.getId() 不能为 null。
     */
    public void delete(Connection conn, Authority auth) throws SQLException {
        run.update(conn
                   , "DELETE FROM authorities WHERE id =?"
                   , auth.getId());
    }

    public List<Authority> findByName( Connection conn, String name)
        throws SQLException {
        ResultSetHandler<List<Authority>> h = new BeanListHandler(Authority.class);
        return run.query(conn
                         , "SELECT id,name,value FROM authorities WHERE name like ? order by name "
                         , h
                         , name+"%");
    }

    public List<Authority> find( Connection conn, Authority auth) throws SQLException {
        ResultSetHandler<List<Authority>> h = new BeanListHandler(Authority.class);
        String sql = "SELECT id,name,value FROM authorities \n";
        try {
            String s = SQLBuilder.beanToSQLClause(auth,"AND");
            sql = sql + SQLBuilder.getWhereClause(s);
        } catch (JibuException e) {
            throw new SQLException(e.getMessage());
        }
        return run.query(conn, sql, h);
    }


    /**
     * {@inheritDoc}
     * <p>
     * user.getId() 不能为 null。
     */
    public List<Authority> find(Connection conn, User user) throws SQLException {
	ResultSetHandler<List<Authority>> h = new BeanListHandler(Authority.class);
	return  run.query(conn
			  ,"SELECT a.id, a.name, a.value "+
			  " FROM roles AS node, roles AS parent, user_role_map AS urm, role_authority_map AS ram, authorities AS a "+
			  " WHERE node.id = urm.role_id "+
			  " AND node.lft BETWEEN parent.lft AND parent.rgt "+
			  " AND parent.id = ram.role_id "+
			  " AND ram.authority_id = a.id "+
			  " AND urm.user_id = ? "
			  , h,user.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * role.getId() 不能为 null。
     */
    public List<Authority> find(Connection conn, Role role) throws SQLException {
	ResultSetHandler<List<Authority>> h = new BeanListHandler(Authority.class);
	return  run.query(conn
			  ,"SELECT a.id, a.name, a.value "+
			  " FROM role_authority_map AS ram, authorities AS a "+
			  " WHERE ram.authority_id = a.id "+
			  " AND ram.role_id = ? "
			  , h,role.getId());
    }
}
