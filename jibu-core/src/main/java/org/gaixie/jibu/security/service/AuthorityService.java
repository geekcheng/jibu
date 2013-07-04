package org.gaixie.jibu.security.service;

import java.util.List;
import java.util.Map;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;

/**
 * Authrity 服务接口。
 * <p>
 */
public interface AuthorityService {

    /**
     * 通过 Authority Id 得到 Authority。
     *
     * @param id Authority Id
     * @return 一个 Authority，如果没有对应的数据，返回 null。
     */
    public Authority get(int id);

    /**
     * 通过 Authority value 与 mask 得到 Authority。
     *
     * @param value Authority value
     * @return 一个 Authority，如果没有对应的数据，返回 null。
     */
    public Authority get(String value);

    /**
     * 增加一个新的 Authority。
     *
     * @param auth Authority
     * @exception JibuException 新增失败时抛出。
     */
    public void add(Authority auth) throws JibuException;

    /**
     * 修改 Authority 。
     * <p>
     * 所有非 null 的属性将被更新。id 属性不能为 null，且不会被更新。
     * @param auth Authority
     * @exception JibuException 修改失败时抛出。
     */
    public void update(Authority auth) throws JibuException;

    /**
     * 得到所有 Authority。
     *
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> getAll();

    /**
     * 根据 username 得到一个包含所拥有权限 name和value的Map。
     * <p>
     * TreeMap 中 如果是最后的叶子节点，则 key = Authority.name, value = Authority.value。
     * 如果是中间节点 key = Authority.name子串， value = #。
     *
     * @param username User username
     * @return 当前用户拥有的权限树。如果 username 没有任何角色对应，返回 size()==0。
     */
    public Map<String,String> findMapByUsername(String username);

    /**
     * 验证 User 对当前操作是否有权限。
     *
     * @param value 用户操作，对应于 Authority 的 value 属性
     * @param username User username
     * @return ture 有权限，false 无权限。
     */
    public boolean verify(String value, String username);

    /**
     * 删除 Authority。
     *
     * @param auth Authority
     * @exception JibuException 删除失败时抛出。
     */
    public void delete(Authority auth) throws JibuException;

    /**
     * 根据 Authority name 模糊查询 Authority。
     *
     * @param name Authority name
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> findByName(String name);

    /**
     * 查询所有匹配给定 Authority 属性的 Authority。
     * <p>
     *
     * @param auth 用来传递查询条件的 Authority。
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> find(Authority auth);

    /**
     * 查询给定 User 所拥有的所有 Authority。
     * <p>
     *
     * @param user User。
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> find(User user);

    /**
     * 查询给定 Role 所拥有的所有 Authority。
     * <p>
     *
     * @param role Role。
     * @return 一个包含 Authority 的 List，无值 size()==0，永远不会返回 null。
     */
    public List<Authority> find(Role role);
}
