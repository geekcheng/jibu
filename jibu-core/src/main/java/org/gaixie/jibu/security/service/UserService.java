package org.gaixie.jibu.security.service;

import java.util.List;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Criteria;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * User 服务接口。
 * <p>
 */
public interface UserService {

    /**
     * 通过 User Id 得到 User。
     *
     * @param id User Id
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(int id);

    /**
     * 通过 username 得到 User。
     * <p>
     *
     * @param username User username。
     * @return 一个 User，如果没有对应的数据，返回 null。
     */
    public User get(String username) ;

    /**
     * 增加一个新 User，password 不能为空，且是没有 hash 过的。
     * <p>
     *
     * @param user User
     * @exception JibuException 新增失败时抛出。
     */
    public void add(User user) throws JibuException;

    /**
     * 修改 User 。
     * <p>
     * 如果要修改密码，user.getPassword() 应该是 hash 前的密码，也就是明文密码 。
     * 所有非 null 的属性将被更新。id 属性不能为 null，且不会被更新。
     * @param user User
     * @exception JibuException 修改失败时抛出。
     */
    public void update(User user) throws JibuException;

    /**
     * 删除给定 User 。
     * <p>
     *
     * @param user User
     * @exception JibuException 删除失败时时抛出。
     */
    public void delete(User user) throws JibuException;

    /**
     * 查询所有匹配给定 User 属性的 User List。
     * <p>
     *
     * @param user 用来传递查询条件的 User。
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(User user);

    /**
     * 查询所有匹配给定 User 属性，并且符合 Criteria 约束的 User。
     * <p>
     * 如果 criteria 为 null，等同于调用 {@code List<User> find(User user)}。<br>
     * @param user 用来传递查询条件的 User。
     * @param criteria 用来传递分页，排序等附加查询条件。
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     * @see #find(User user)
     */
    public List<User> find(User user, Criteria criteria);

    /**
     * 查询所有直接绑定给定 Role 的 User，不包括继承关系。
     * <p>
     *
     * @param role Role
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Role role);

    /**
     * 查询所有拥有给定 Authority 的 User。
     * <p>
     *
     * @param auth Authority
     * @return 一个包含 User 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<User> find(Authority auth);
}
