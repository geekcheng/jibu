package org.gaixie.jibu.security.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * Authority 数据访问对象接口。
 * <p>
 */
public interface AuthorityDAO {

    /**
     * 通过 Authority Id 得到一个 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param id Authority id。
     *
     * @throws SQLException
     * @return 一个 Authority，如果没有对应的数据，返回 null。
     */
    public Authority get(Connection conn, int id) throws SQLException;

    /**
     * 通过 Authority value 得到一个 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param value Authority value。
     *
     * @throws SQLException
     * @return 一个 Authority，如果没有对应的数据，返回 null。
     */
    public Authority get(Connection conn, String value) throws SQLException;

    /**
     * 增加一个 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param auth Authority。
     *
     * @throws SQLException
     */
    public void save(Connection conn, Authority auth) throws SQLException;

    /**
     * 得到所有 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     *
     * @throws SQLException
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> getAll(Connection conn) throws SQLException;

    /**
     * 删除一个 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param auth 需要删除的 Authority。
     *
     * @throws SQLException
     */
    public void delete(Connection conn, Authority auth) throws SQLException;

    /**
     * 更新除 id 以外的所有 Authority 属性。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param auth 需要更新的 Authority。
     *
     * @throws SQLException
     */
    public void update(Connection conn, Authority auth) throws SQLException;

    /**
     * 根据 Authority name 模糊查询( like 'name%') Authority 。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param name Authority name。
     *
     * @throws SQLException
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> findByName(Connection conn,String name) throws SQLException;

    /**
     * 根据 Authority 属性查询符合条件的 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param auth Authority，非 null 的属性值会作为查询匹配的条件。
     *
     * @throws SQLException
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> find(Connection conn, Authority auth) throws SQLException;

    /**
     * 查询给定 User 所拥有的 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param user User。
     *
     * @throws SQLException
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> find(Connection conn, User user) throws SQLException;

    /**
     * 查询给定 Role 所拥有的 Authority。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param role Role。
     *
     * @throws SQLException
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> find(Connection conn, Role role) throws SQLException;
}

