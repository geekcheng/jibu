package org.gaixie.jibu.security.service.impl;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.dbutils.DbUtils;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.cache.Cache;
import org.gaixie.jibu.utils.CacheUtils;
import org.gaixie.jibu.utils.ConnectionUtils;
import org.gaixie.jibu.security.dao.AuthorityDAO;
import org.gaixie.jibu.security.dao.RoleDAO;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authority 服务接口的默认实现。
 * <p>
 */
public class AuthorityServiceImpl implements AuthorityService {
    Logger logger = LoggerFactory.getLogger(AuthorityServiceImpl.class);
    private final AuthorityDAO authDAO;
    private final RoleDAO roleDAO;

    /**
     * 使用 Guice 进行 DAO 的依赖注入。
     * <p>
     */
    @Inject
    public AuthorityServiceImpl(AuthorityDAO authDAO,
                                RoleDAO roleDAO) {
        this.authDAO = authDAO;
        this.roleDAO = roleDAO;
    }

    public Authority get(int id) {
        Connection conn = null;
	Authority auth = null;
        try {
            conn = ConnectionUtils.getConnection();
            auth = authDAO.get(conn,id);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auth;
    }

    public Authority get(String value) {
        Connection conn = null;
	Authority auth = null;
        try {
            conn = ConnectionUtils.getConnection();
            auth = authDAO.get(conn,value);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auth;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 此操作会清空 Cache 中 authorities 对应的 value，下次请求时装载。
     * @see #getAll()
     */
    public void add(Authority auth) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
	    authDAO.save(conn,auth);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getAuthCache();
	    cache.remove("authorities");
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 如果 Cache 中的 authorities 对应的 value 为空，从 DAO 得到最新的
     * value 并装入 Cache。
     */
    public List<Authority> getAll() {
        Connection conn = null;
	List<Authority> auths = null;

	Cache cache = CacheUtils.getAuthCache();
	auths = (List<Authority>)cache.get("authorities");
	if (null != auths) return auths;
        try {
            conn = ConnectionUtils.getConnection();
            auths = authDAO.getAll(conn);
	    cache.put("authorities",auths);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auths;
    }

    private List<String> findRoleNamesByUsername(String username) {
        Connection conn = null;
	Cache cache = CacheUtils.getUserCache();
	List<String> names = (List<String>)cache.get(username);
	if (null != names) return names;

        try {
            conn = ConnectionUtils.getConnection();
            names = roleDAO.findByUsername(conn,username);
	    cache.put(username,names);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return names;
    }

    private void putTreeMap(Map<String,String> map, Authority auth) {
        String[] sp = auth.getName().split("\\.");
        String key = "";
        for (int i =0; i< sp.length -1; i++) {
            key = (i==0) ? sp[i] : key +"." + sp[i];
            map.put(key,"#");
        }
        map.put(auth.getName(), auth.getValue());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 初次调用会访问数据库，之后就访问 Cache，直到 Cache 更新。<br>
     * 将所有的 Authority 绑定的角色与 username 拥有的角色进行匹配，
     * 成功则将 装入 Map。<br>
     * 约定如果 authority.name 的长度小于 5位，将不包括在返回的 Map中。
     * 也就是说，不用于菜单的显示，但权限验证依然有效。
     */
    public Map<String,String> findMapByUsername(String username) {
	Map<String,String> treemap = new TreeMap<String,String>();
	List<Authority> auths = getAll();

	List<String> urole = findRoleNamesByUsername(username);
        if (null== urole) return treemap;

	for (Authority auth : auths) {
            if (auth.getName().length() < 5) continue;
            // 写死ROLE_ADMIN角色无须任何判断，加载所有数据
            if (urole.contains("ROLE_ADMIN")) {
                putTreeMap(treemap,auth);
		continue;
            }

	    List<String> arole = findRoleNamesByValue(auth.getValue());
            // map.size()==0 ，表示auth还没绑定任何Role，所有用户都有权限
	    if (arole.size()==0) {
                putTreeMap(treemap,auth);
		continue;
	    }
            for (String role : arole) {
                if (urole.contains(role)) {
                    putTreeMap(treemap,auth);
                    break;
                }
            }
	}
	return treemap;
    }


    private List<String> findRoleNamesByValue(String value) {
        Connection conn = null;
	Cache cache = CacheUtils.getAuthCache();
	List<String> names = (List<String>)cache.get(value);
	if (null != names) return names;
        try {
            conn = ConnectionUtils.getConnection();
            Authority auth = authDAO.get(conn,value);
            if (null != auth) {
                names = roleDAO.findByAuthid(conn,auth.getId());
                cache.put(value,names);
            }
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return names;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 初次调用会访问数据库，之后就访问 Cache，直到 Cache 更新。<br>
     * 权限验证有下面一些默认约定：
     * <ul>
     * <li>如果 username 没有绑定任何 Role，即没有任何权限，返回 false。</li>
     * <li>如果 username 有管理员角色 ROLE_ADMIN ，即拥有所有权限，返回 true。</li>
     * <li>通过 value 没有找到任何匹配的 Authority，表示该操作无效，
     * 返回 false。</li>
     * <li>如果匹配的 Authority 没有绑定任何 Role，认为它不受权限控制，
     * 返回 true。</li>
     * </ul>
     *
     */
    public boolean verify(String value, String username) {
        List<String> userRoles = findRoleNamesByUsername(username);
        if ( null == userRoles) return false;
        if (userRoles.contains("ROLE_ADMIN")) return true;
        List<String> authRoles = findRoleNamesByValue(value);
        // 要想暂时禁止所有用户访问，就只将此auth绑定到一个没有任何用户的角色上
        if ( null == authRoles ) return false;
        if ( authRoles.size()==0 ) return true;

        for (String role: authRoles) {
            if(userRoles.contains(role)) {
                return true;
            }
        }
	return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 此操作会清空 Cache 中 authorities 对应的 value，下次请求时装载。
     * @see #getAll()
     */
    public void delete(Authority auth) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
	    authDAO.delete(conn,auth);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getAuthCache();
	    cache.remove("authorities");
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 此操作会清空 Cache 中 authorities 对应的 value 和
     * key = auth.getValue() 的数据，下次请求时装载。
     * @see #getAll()
     */
    public void update(Authority auth) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            Authority old = authDAO.get(conn,auth.getId());
	    authDAO.update(conn,auth);
            DbUtils.commitAndClose(conn);
	    Cache cache = CacheUtils.getAuthCache();
	    cache.remove("authorities");
            cache.remove(old.getValue());
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public List<Authority> findByName(String name) {
        Connection conn = null;
	List<Authority> auths = null;
        try {
            conn = ConnectionUtils.getConnection();
            auths = authDAO.findByName(conn,name);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auths;
    }

    public List<Authority> find(Authority auth) {
        if (null == auth) return this.getAll();
        Connection conn = null;
	List<Authority> auths = null;
        try {
            conn = ConnectionUtils.getConnection();
            auths = authDAO.find(conn,auth);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auths;
    }

    public List<Authority> find(User user) {
        Connection conn = null;
	List<Authority> auths = null;
        try {
            conn = ConnectionUtils.getConnection();
            auths = authDAO.find(conn,user);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auths;
    }

    public List<Authority> find(Role role) {
        Connection conn = null;
	List<Authority> auths = null;
        try {
            conn = ConnectionUtils.getConnection();
            auths = authDAO.find(conn,role);
        } catch(SQLException e) {
	    logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
	return auths;
    }
}
