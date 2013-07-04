package org.gaixie.jibu.security.service;

import java.util.List;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.model.User;

/**
 * Setting 服务接口。
 * <p>
 */
public interface SettingService {

    /**
     * 通过 Setting Id 得到 Setting。
     *
     * @param id Setting Id
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting get(int id);

    /**
     * 通过 name, value 得到一个 Setting。
     * <p>
     *
     * @param name Setting name。
     * @param value Setting value。
     *
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting get(String name, String value);

    /**
     * 通过 name 得到一个 默认的Setting。
     * <p>
     *
     * @param name Setting name。
     *
     * @return 一个 Setting，如果 name 不存在，返回 null。
     */
    public Setting getDefault(String name);

    /**
     * 给定 name ，得到 username 选定的 Setting。
     * <p>
     *
     * @param name Setting name。
     * @param username User username。
     * @return 一个 Setting，如果没有对应的数据，返回 null。
     */
    public Setting getByUsername(String name, String username);

    /**
     * 增加一个新 Setting。
     * <p>
     *
     * @param setting Setting
     * @exception JibuException 新增失败时抛出。
     */
    public void add(Setting setting) throws JibuException;

    /**
     * 修改 Setting 。
     * <p>
     *
     * @param setting Setting
     * @exception JibuException 修改失败时抛出。
     */
    public void update(Setting setting) throws JibuException;

    /**
     * 删除给定 Setting 。
     * <p>
     *
     * @param setting Setting
     * @exception JibuException 删除失败时时抛出。
     */
    public void delete(Setting setting) throws JibuException;

    /**
     * 保存给定 username 与所选择的 Setting 的对应关系。
     * <p>
     *
     * @param sid Setting id。
     * @param username User username。
     */
    public void bind(int sid, String username) throws JibuException;

    /**
     * 删除给定 username 与所选择的 Setting 的对应关系。
     * <p>
     *
     * @param sid Setting id。
     * @param username User username。
     */
    public void unbind(int sid, String username) throws JibuException;

    /**
     * 保存给定 username 所有选择的 Setting 的对应关系，同时更新 user的相关属性。
     * <p>
     *
     * @param ids Setting id List。
     * @param user User。
     */
    public void updateMe(List<Integer> ids, User user) throws JibuException;

    /**
     * 将 username 的所有 Setting 恢复为默认值。
     * <p>
     *
     * @param username User username。
     */
    public void reset(String username) throws JibuException;

    /**
     * 得到 name 对应的一组 Setting。
     * <p>
     * 结果集按 sortindex 排序。
     *
     * @param name Setting name。
     *
     * @return 一个包含 Setting 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Setting> findByName(String name);

   /**
     * 得到 username 当前的 Setting。
     * <p>
     * 没有做过选择的配置，返回的是默认的配置。
     *
     * @param username User username。
     *
     * @return 一个包含 Setting 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Setting> findByUsername(String username);
}
