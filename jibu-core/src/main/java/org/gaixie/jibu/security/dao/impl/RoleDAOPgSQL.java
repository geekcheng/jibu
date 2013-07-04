package org.gaixie.jibu.security.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.utils.SQLBuilder;
import org.gaixie.jibu.security.dao.RoleDAO;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * Role 数据访问接口的 PostgreSQL 实现。
 * <p>
 */
public class RoleDAOPgSQL implements RoleDAO {
    private QueryRunner run = null;

    public RoleDAOPgSQL() {
	this.run = new QueryRunner();
    }

    public Role get(Connection conn, int id) throws SQLException {
	ResultSetHandler<Role> h = new BeanHandler(Role.class);
	return run.query(conn
			 , "SELECT id, name, description, lft, rgt FROM roles WHERE id=? "
			 , h
			 , id);
    }

    public Role get(Connection conn, String name) throws SQLException {
	ResultSetHandler<Role> h = new BeanHandler(Role.class);
	return run.query(conn
			 , "SELECT id, name, description, lft, rgt FROM roles WHERE name=? "
			 , h
			 , name);
    }

    public void save(Connection conn, Role role, Role parent) throws SQLException {
	if (null==parent) return ;
	run.update(conn
		   , "UPDATE roles set lft=lft+2 where lft > ?"
		   , parent.getLft());
	run.update(conn
		   , "UPDATE roles set rgt=rgt+2 where rgt > ?"
		   , parent.getLft());
	run.update(conn
		   , "INSERT INTO roles (name,description,lft,rgt) values (?,?,?,?)"
		   , role.getName()
		   , role.getDescription()
		   , parent.getLft()+1
		   , parent.getLft()+2);
    }


    /**
     * {@inheritDoc}
     * <p>
     * 除 id 属性以外，所有非空的属性将会被更新至数据库中。
     */
    public void update(Connection conn, Role role) throws SQLException {
        String sql = "UPDATE roles \n";
        Integer id = role.getId();
        role.setId(null);
        try {
            String s = SQLBuilder.beanToSQLClause(role,",");
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
     * role.getId() 不能为 null。
     */
    public void delete(Connection conn, Role role) throws SQLException {
        run.update(conn
                   , "DELETE FROM roles WHERE id =?"
                   , role.getId());
	run.update(conn
		   , "UPDATE roles set lft=lft-2 where lft > ?"
		   , role.getLft());
	run.update(conn
		   , "UPDATE roles set rgt=rgt-2 where rgt > ?"
		   , role.getLft());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 按 lft 排序。
     */
    public List<Role> getAll(Connection conn) throws SQLException {
	ResultSetHandler<List<Role>> h = new BeanListHandler(Role.class);
	return  run.query(conn
			  ,"SELECT node.id, node.name, node.description, node.lft, node.rgt,(COUNT(parent.name)-1) AS depth "+
			  " FROM roles AS node, roles AS parent "+
			  " WHERE node.lft BETWEEN parent.lft AND parent.rgt "+
                          " GROUP BY node.id, node.name, node.description, node.lft, node.rgt "+
			  " ORDER BY node.lft"
			  , h);
    }

    /**
     * {@inheritDoc}
     * <p>
     * role.getId() 与 auth.getId() 不能为 null。
     */
    public void bind(Connection conn, Role role, Authority auth) throws SQLException {
	run.update(conn
		   , "INSERT INTO role_authority_map (role_id,authority_id) values (?,?)"
		   , role.getId()
		   , auth.getId());

    }

    /**
     * {@inheritDoc}
     * <p>
     * auth.getId() 不能为 null。
     */
    public List<Role> find(Connection conn, Authority auth) throws SQLException {
	ResultSetHandler<List<Role>> h = new BeanListHandler(Role.class);
	return  run.query(conn
			  ,"SELECT node.id, node.name, node.description, node.lft, node.rgt "+
			  " FROM roles AS node, role_authority_map AS ram "+
			  " WHERE node.id = ram.role_id "+
			  " AND ram.authority_id = ? "
			  , h,auth.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * role.getId() 与 user.getId() 不能为 null。
     */
    public void bind(Connection conn, Role role, User user) throws SQLException {
	run.update(conn
		   , "INSERT INTO user_role_map (user_id,role_id) values (?,?)"
		   , user.getId()
		   , role.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * role.getId() 与 user.getId() 不能为 null。
     */
    public void unbind(Connection conn, Role role, User user) throws SQLException {
	run.update(conn
		   , "DELETE FROM user_role_map WHERE user_id=? and role_id=?"
		   , user.getId()
		   , role.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * role.getId() 与 auth.getId() 不能为 null。
     */
    public void unbind(Connection conn, Role role, Authority auth) throws SQLException {
	run.update(conn
		   , "DELETE FROM role_authority_map WHERE authority_id=? and role_id=?"
		   , auth.getId()
		   , role.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * user.getId() 不能为 null。
     */
    public List<Role> find(Connection conn, User user) throws SQLException {
	ResultSetHandler<List<Role>> h = new BeanListHandler(Role.class);
	return  run.query(conn
			  ,"SELECT node.id, node.name, node.description, node.lft, node.rgt "+
			  " FROM roles AS node, user_role_map AS urm "+
			  " WHERE node.id = urm.role_id "+
			  " AND urm.user_id = ? "
			  , h,user.getId());
    }

    public List<String> findByAuthid(Connection conn, int id) throws SQLException {
	ResultSetHandler<List<String>> h = new ResultSetHandler<List<String>>() {
	    public List<String> handle(ResultSet rs) throws SQLException {
		List<String> result = new ArrayList<String>();
		while(rs.next()) {
		    result.add(rs.getString(1));
		}
		return result;
	    }
	};
	return  run.query(conn
			  ,"SELECT r.name "+
			  " FROM roles AS r, role_authority_map AS ram "+
			  " WHERE r.id = ram.role_id "+
			  " AND ram.authority_id =? "
			  , h
                          , id);
    }

    public List<String> findByUsername(Connection conn, String username) throws SQLException {
	ResultSetHandler<List<String>> h = new ResultSetHandler<List<String>>() {
	    public List<String> handle(ResultSet rs) throws SQLException {
		List<String> result = new ArrayList<String>();
		while(rs.next()) {
		    result.add(rs.getString(1));
		}
		int len = result.size();
		if (len <=0) return null;
		return result;
	    }
	};

	return  run.query(conn
			  ,"SELECT parent.name "+
			  " FROM roles AS node, roles AS parent, user_role_map AS urm, userbase u "+
			  " WHERE node.id = urm.role_id "+
			  " AND node.lft BETWEEN parent.lft AND parent.rgt "+
			  " AND u.id = urm.user_id " +
			  " AND u.username = ? "
			  , h,username);
    }

}
