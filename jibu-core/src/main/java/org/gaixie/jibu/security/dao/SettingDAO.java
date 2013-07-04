package org.gaixie.jibu.security.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.model.User;

/**
 * Setting 数据访问对象接口。
 * <p>
 *
 */
public interface SettingDAO {

    /**
     * 通过 id 得到一个 Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param id Setting id。
     *
     * @throws SQLException
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting get(Connection conn, int id) throws SQLException;

    /**
     * 通过 name, value 得到一个 Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param name Setting name。
     * @param value Setting value。
     *
     * @throws SQLException
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting get(Connection conn, String name, String value) throws SQLException;

    /**
     * 通过 name 得到一个 默认的Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param name Setting name。
     *
     * @throws SQLException
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting getDefault(Connection conn, String name) throws SQLException;

    /**
     * 给定 name ，得到 username 选定的 Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param name Setting name。
     * @param username User username。
     *
     * @throws SQLException
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting getByUsername(Connection conn, String name, String username) throws SQLException;

    /**
     * 增加一个新 Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param setting Setting。
     *
     * @throws SQLException
     */
    public void save(Connection conn, Setting setting) throws SQLException;

    /**
     * 保存给定 User 与所选择的 Setting 的对应关系。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param sid Setting id。
     * @param uid User id。
     *
     * @throws SQLException
     */
    public void bind(Connection conn, int sid, int uid) throws SQLException;

    /**
     * 删除给定 User 与所选择的 Setting 的对应关系。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param sid Setting id。
     * @param uid User id。
     *
     * @throws SQLException
     */
    public void unbind(Connection conn, int sid, int uid) throws SQLException;

    /**
     * 删除给定 User 与所有选择的 Setting 的对应关系。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param uid User id。
     *
     * @throws SQLException
     */
    public void unbindAll(Connection conn, int uid) throws SQLException;

    /**
     * 更新 Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param setting Setting。
     *
     * @throws SQLException
     */
    public void update(Connection conn, Setting setting) throws SQLException;

    /**
     * 删除 Setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param setting Setting。
     *
     * @throws SQLException
     */
    public void delete(Connection conn, Setting setting) throws SQLException;

    /**
     * 得到 name 对应的一组 Setting。
     * <p>
     * 结果集按 sortindex 排序。
     *
     * @param conn 一个有效的数据库链接。
     * @param name Setting name。
     *
     * @throws SQLException
     * @return 一个包含 Setting 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Setting> findByName(Connection conn, String name) throws SQLException;

    /**
     * 得到所有默认的setting。
     * <p>
     *
     * @param conn 一个有效的数据库链接
     *
     * @throws SQLException
     * @return 一个包含 Setting 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Setting> getAllDefault(Connection conn) throws SQLException;

    /**
     * 得到给定 username 已配置的 setting，不包括默认配置。
     * <p>
     *
     * @param conn 一个有效的数据库链接。
     * @param username User username。
     *
     * @throws SQLException
     * @return 一个包含 Setting 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Setting> findByUsername(Connection conn,String username) throws SQLException;
}