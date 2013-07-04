package org.gaixie.jibu.security.service.impl;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.cache.Cache;
import org.gaixie.jibu.utils.CacheUtils;
import org.gaixie.jibu.utils.ConnectionUtils;
import org.gaixie.jibu.security.dao.AuthorityDAO;
import org.gaixie.jibu.security.dao.RoleDAO;
import org.gaixie.jibu.security.dao.UserDAO;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.RoleException;
import org.gaixie.jibu.security.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Role 服务接口的默认实现。
 * <p>
 */
public class RoleServiceImpl implements RoleService {
    Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleDAO roleDAO;
    private final AuthorityDAO authDAO;
    private final UserDAO userDAO;

    /**
     * 使用 Guice 进行 DAO 的依赖注入。
     * <p>
     */
    @Inject
    public RoleServiceImpl(RoleDAO roleDAO,
                           UserDAO userDAO,
                           AuthorityDAO authDAO) {
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Role get(int id) {
        Connection conn = null;
	Role role = null;
        try {
            conn = ConnectionUtils.getConnection();
            role = roleDAO.get(conn, id);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return role;
    }

    public Role get(String name) {
        Connection conn = null;
	Role role = null;
        try {
            conn = ConnectionUtils.getConnection();
            role = roleDAO.get(conn, name);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return role;
    }

    public void add(Role role, Role parent) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
	    roleDAO.save(conn, role,parent);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void delete(Role role) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            role = roleDAO.get(conn,role.getId());
            if ((role.getRgt()-role.getLft()) > 1)
                throw new RoleException("The role is not leaf node.");
	    roleDAO.delete(conn,role);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }

    }

    /**
     * {@inheritDoc}
     * <p>
     * 如果修改 Role，那么所有用于权限验证的 Cache 要重置。
     */
    public void update(Role role) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            // 更新时不能破化树形结构
            role.setLft(null);
            role.setRgt(null);
	    roleDAO.update(conn,role);
            DbUtils.commitAndClose(conn);
	    CacheUtils.getAuthCache().clear();
	    CacheUtils.getUserCache().clear();
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public List<Role> getAll() {
        Connection conn = null;
	List<Role> roles = null;
        try {
            conn = ConnectionUtils.getConnection();
            roles = roleDAO.getAll(conn);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return roles;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 从 cache 中删除此 auth 对应的 roles。下次条用时装载。
     */
    public void bind(Role role, Authority auth) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            Authority a = authDAO.get(conn,auth.getId());
	    roleDAO.bind(conn, role, a);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getAuthCache();
	    cache.remove(a.getValue());
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public List<Role> find(Authority auth) {
        Connection conn = null;
	List<Role> roles = null;

        try {
            conn = ConnectionUtils.getConnection();
            roles = roleDAO.find(conn,auth);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return roles;

    }

    /**
     * {@inheritDoc}
     * <p>
     * 从 cache 中删除此 User 对应的 roles。下次条用时装载。
     */
    public void bind(Role role, User user) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            User u = userDAO.get(conn,user.getId());
	    roleDAO.bind(conn,role,u);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getUserCache();
	    cache.remove(u.getUsername());
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public List<Role> find(User user) {
        Connection conn = null;
	List<Role> roles = null;
        try {
            conn = ConnectionUtils.getConnection();
            roles = roleDAO.find(conn,user);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return roles;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 从 cache 中删除此 auth 对应的 roles。
     */
    public void unbind(Role role, Authority auth) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            Authority a = authDAO.get(conn,auth.getId());
	    roleDAO.unbind(conn, role, a);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getAuthCache();
	    cache.remove(a.getValue());
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 从 cache 中删除此 User 对应的 roles。
     */
    public void unbind(Role role, User user) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            User u = userDAO.get(conn,user.getId());
	    roleDAO.unbind(conn,role,u);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getUserCache();
	    cache.remove(u.getUsername());
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }
}
