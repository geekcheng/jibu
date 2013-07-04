package org.gaixie.jibu.security.service;

import java.util.List;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * Role 服务接口。
 * <p>
 */
public interface RoleService {

    /**
     * 通过 Role id 得到 Role。
     * <p>
     *
     * @param id Role id。
     * @return 一个 Role，如果没有对应的数据，返回 null。
     */
    public Role get(int id) ;

    /**
     * 通过 Role name 得到 Role 。
     * <p>
     *
     * @param name Role name。
     * @return 一个 Role，如果没有对应的数据，返回 null。
     */
    public Role get(String name) ;

    /**
     * 为给定 Role 增加一个新的子 Role 。
     * <p>
     *
     * @param role 新 Role。
     * @param parent 父 Role。
     * @exception JibuException 新增失败时抛出。
     */
    public void add(Role role, Role parent) throws JibuException;

    /**
     * 修改 Role 。
     * <p>
     * 所有非 null 的属性将被更新。id 属性不能为 null，且不会被更新。
     * @param role Role
     * @exception JibuException 修改失败时抛出。
     */
    public void update(Role role) throws JibuException;

    /**
     * 删除一个叶子 Role。
     *
     * @param role Role
     * @exception JibuException 删除失败时抛出。
     */
    public void delete(Role role) throws JibuException;

    /**
     * 得到一个完整的 Role List。
     * <p>
     *
     * @return Role List
     */
    public List<Role> getAll();

    /**
     * 将给定 Role 与 Authorit 进行绑定。
     * <p>
     *
     * @param role Role。
     * @param auth Authority，auth.getId() 不能为 null。
     * @exception JibuException 如果操作出现并发修改抛出。
     */
    public void bind(Role role, Authority auth) throws JibuException;

    /**
     * 给定 Authority，查询与之绑定的 Role。
     * <p>
     *
     * @param auth Authority。
     * @return Role List
     */
    public List<Role> find(Authority auth);

    /**
     * 将给定 Role 与 User 进行绑定。
     * <p>
     *
     * @param role Role。
     * @param user User，user.getId() 不能为 null。
     * @exception JibuException 如果操作出现并发修改抛出。
     */
    public void bind(Role role, User user) throws JibuException;

    /**
     * 给定 User，查询与之绑定的 Role。
     * <p>
     *
     * @param user User。
     * @return Role List
     */
    public List<Role> find(User user);

    /**
     * 将给定 Role 与 Authorit 解除绑定。
     * <p>
     *
     * @param role Role。
     * @param auth Authority，auth.getId() 不能为 null。
     * @exception JibuException 如果操作出现并发修改抛出。
     */
    public void unbind(Role role, Authority auth) throws JibuException;

    /**
     * 将给定 Role 与 User 解除绑定。
     * <p>
     *
     * @param role Role。
     * @param user User，user.getId() 不能为 null。
     * @exception JibuException 如果操作出现并发修改抛出。
     */
    public void unbind(Role role, User user) throws JibuException;
}
