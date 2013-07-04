package org.gaixie.jibu.security.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.gaixie.jibu.security.model.Criteria;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * User 数据访问对象接口。
 * <p>
 *
 */
public interface UserDAO {

    /**
     * 通过 id 得到一个 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param id User id。
     *
     * @throws SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(Connection conn, int id) throws SQLException;

    /**
     * 通过 username 得到一个 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param username User username。
     *
     * @throws SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(Connection conn, String username) throws SQLException;


    /**
     * 通过 username 及 password 得到一个 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param username User username。
     * @param password hash 后的 password。
     *
     * @throws SQLException
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User login(Connection conn, String username, String password) throws SQLException;

    /**
     * 增加一个新 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User。
     *
     * @throws SQLException
     */
    public void save(Connection conn, User user) throws SQLException;

    /**
     * 更新 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User。
     *
     * @throws SQLException
     */
    public void update(Connection conn, User user) throws SQLException;

    /**
     * 删除 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User。
     *
     * @throws SQLException
     */
    public void delete(Connection conn, User user) throws SQLException;

    /**
     * 根据 User 属性查询符合条件的 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User，属性值会作为查询匹配的条件。
     *
     * @throws SQLException
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Connection conn, User user) throws SQLException;

    /**
     * 根据 User 属性得到符合条件的记录总数。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User，属性值会作为查询匹配的条件。
     *
     * @throws SQLException
     * @return 记录总数
     */
    public int getTotal(Connection conn, User user) throws SQLException;

    /**
     * 根据 User 属性查询符合条件，并且满足 Criteria 约束的 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User，属性值会作为查询匹配的条件。
     * @param criteria 传递分页，排序等附加的查询条件。
     *
     * @throws SQLException
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Connection conn, User user, Criteria criteria) throws SQLException;

    /**
     * 查询拥有给定 Role 的 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role Role。
     *
     * @throws SQLException
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Connection conn, Role role) throws SQLException;

    /**
     * 查询拥有给定 Authority 的 User。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param auth Authority。
     *
     * @throws SQLException
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Connection conn, Authority auth) throws SQLException;
}