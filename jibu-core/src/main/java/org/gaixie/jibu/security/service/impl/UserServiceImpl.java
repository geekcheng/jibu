package org.gaixie.jibu.security.service.impl;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.MD5;
import org.gaixie.jibu.security.dao.UserDAO;
import org.gaixie.jibu.security.model.Criteria;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User 服务接口的默认实现。
 * <p>
 */
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;

    /**
     * 使用 Guice 进行 DAO 的依赖注入。
     * <p>
     */
    @Inject
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。
     */
    public User get(int id) {
        Connection conn = null;
        User user = null;
        try {
            conn = ConnectionUtils.getConnection();
            user = userDAO.get(conn,id);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，password 属性为 null。
     */
    public User get(String username) {
        Connection conn = null;
        User user = null;
        try {
            conn = ConnectionUtils.getConnection();
            user = userDAO.get(conn,username);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return user;
    }

    public void add(User user) throws JibuException {
        Connection conn = null;
        if (null != user.getPassword()) {
            String cryptpassword = MD5.encodeString(user.getPassword(),null);
            user.setPassword(cryptpassword);
        }
        try {
            conn = ConnectionUtils.getConnection();
            userDAO.save(conn,user);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void update(User user) throws JibuException {
        Connection conn = null;
        if (null != user.getPassword()) {
            String cryptpassword = MD5.encodeString(user.getPassword(),null);
            user.setPassword(cryptpassword);
        }

        try {
            conn = ConnectionUtils.getConnection();
            userDAO.update(conn,user);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void delete(User user) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            userDAO.delete(conn,user);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。
     */
    public List<User> find(User user) {
        Connection conn = null;
        List<User> users= null;
        if (user.getPassword()!=null) {
            String cryptpassword = MD5.encodeString(user.getPassword(),null);
            user.setPassword(cryptpassword);
        }
        try {
            conn = ConnectionUtils.getConnection();
            users = userDAO.find(conn,user);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return users;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。
     */
    public List<User> find(User user, Criteria criteria) {
        if (null == criteria ) return this.find(user);
        Connection conn = null;
        List<User> users= null;
        if (user.getPassword() != null) {
            String cryptpassword = MD5.encodeString(user.getPassword(),null);
            user.setPassword(cryptpassword);
        }
        try {
            conn = ConnectionUtils.getConnection();
            if (criteria.getLimit() >0) {
                criteria.setTotal(userDAO.getTotal(conn,user));
            }
            users = userDAO.find(conn,user,criteria);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return users;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。
     */
    public List<User> find(Role role) {
        Connection conn = null;
        List<User> users= null;
        try {
            conn = ConnectionUtils.getConnection();
            users = userDAO.find(conn,role);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return users;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 安全考虑，返回 List 中所有 User 的 password 属性为 null。
     */
    public List<User> find(Authority auth) {
        Connection conn = null;
        List<User> users= null;
        try {
            conn = ConnectionUtils.getConnection();
            users = userDAO.find(conn,auth);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return users;
    }
}
