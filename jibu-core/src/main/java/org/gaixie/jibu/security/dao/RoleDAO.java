package org.gaixie.jibu.security.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * Role 数据访问对象接口。
 * <p>
 */
public interface RoleDAO {

    /**
     * 通过 Role id 得到一个 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param id Role id。
     *
     * @throws SQLException
     * @return 一个 Role，如果没有对应的数据，返回 null。
     */
    public Role get(Connection conn, int id) throws SQLException;

    /**
     * 通过 Role name 得到一个 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param name Role name。
     *
     * @throws SQLException
     * @return 一个 Role，如果没有对应的数据，返回 null。
     */
    public Role get(Connection conn, String name) throws SQLException;

    /**
     * 为给定 Role 增加一个孩子 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role 要新增的 Role。
     * @param parent 给定的父 Role。
     *
     * @throws SQLException
     */
    public void save(Connection conn, Role role, Role parent) throws SQLException ;


    /**
     * 删除一个 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role 需要删除的 Role。
     *
     * @throws SQLException
     */
    public void delete(Connection conn, Role role) throws SQLException;

    /**
     * 更新除 id 以外的所有 Role 属性。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role 需要更新的 Role。
     *
     * @throws SQLException
     */
    public void update(Connection conn, Role role) throws SQLException;

    /**
     * 得到全部 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     *
     * @throws SQLException
     * @return 一个包含 Role 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Role> getAll(Connection conn) throws SQLException ;

    /**
     * 将 Role 和 Authority 进行绑定。
     *
     * @param conn 一个有效的数据库链接。
     * @param role Role。
     * @param auth Authority。
     *
     * @throws SQLException
     */
    public void bind(Connection conn, Role role, Authority auth) throws SQLException;

    /**
     * 通过给定 Authority，得到所有已经与其绑定的 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param auth Authority。
     *
     * @throws SQLException
     * @return 一个包含 Role 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Role> find(Connection conn, Authority auth) throws SQLException;

    /**
     * 将 Role 和 User 进行绑定。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role 角色
     * @param user 用户
     *
     * @throws SQLException
     */
    public void bind(Connection conn, Role role, User user) throws SQLException;

    /**
     * 通过给定 User，得到所有已经与其绑定的 Role。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User。
     *
     * @throws SQLException
     * @return 一个包含 Role 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Role> find(Connection conn, User user) throws SQLException;

    /**
     * 通过 authority id 取得匹配的Role name。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param id Authority id。
     *
     * @throws SQLException
     * @return 一个包含 Role name 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<String> findByAuthid(Connection conn, int id) throws SQLException;

    /**
     * 通过 username 取得匹配的 Role name。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param name User username。
     *
     * @throws SQLException
     * @return 一个包含 Role name 的 List，如果没有任何匹配的数据，返回 null。
     */
    public List<String> findByUsername(Connection conn, String name) throws SQLException;

    /**
     * 将指定的 Role 和 Authority 解除绑定。
     *
     * @param conn 一个有效的数据库链接。
     * @param role Role。
     * @param auth Authority。
     *
     * @throws SQLException
     */
    public void unbind(Connection conn, Role role, Authority auth) throws SQLException;


    /**
     * 将指定的 Role 和 User 解除绑定。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role 角色
     * @param user 用户
     *
     * @throws SQLException
     */
    public void unbind(Connection conn, Role role, User user) throws SQLException;
}
