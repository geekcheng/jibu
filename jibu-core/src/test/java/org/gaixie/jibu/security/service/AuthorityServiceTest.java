package org.gaixie.jibu.security.service;


import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.cache.Cache;
import org.gaixie.jibu.security.SecurityTestSupport;
import org.gaixie.jibu.security.model.Authority;
import org.gaixie.jibu.security.model.Role;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.AuthorityService;
import org.gaixie.jibu.security.service.RoleService;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.CacheUtils;
import org.gaixie.jibu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthorityServiceTest extends SecurityTestSupport {
    private AuthorityService authService;
    private RoleService roleService;
    private UserService userService;

    @Before
    public void setup() throws Exception {
	clearTable();
        authService = getInjector().getInstance(AuthorityService.class); 
        roleService = getInjector().getInstance(RoleService.class); 
        userService = getInjector().getInstance(UserService.class); 

        userService.add(new User("Administrator","admin","123456","jibu.gaixie@gmail.com",1,true));

        authService.add(new Authority("sec.ast-v-auth1","/ast-v-auth1.z"));
        authService.add(new Authority("sec.ast-v-auth2","/ast-v-auth2.z"));
        authService.add(new Authority("sec.ast-v-auth3","/ast-v-auth3.z"));
        // 这是一个特殊的权限(value.length <5)，不用于菜单的显示，但用于权限验证
        authService.add(new Authority("ci","/ast-v-auth1.z?ci=addUser"));
        userService.add(new User("ast-v-user1","ast-v-user1","123456","ast-v-user1@x.xxx",1,true));

        /*
         * 建立如下角色继承关系
         * ROLE_BASE
         *     |-----ROLE_ADMIN
         *     |
         *     |-----ast-v-role1
         *     |          |------ast-v-role2
         *     |-----ast-v-role3
         */
        // 先插入一个 Role 的根节点。
        Connection conn = ConnectionUtils.getConnection();
        QueryRunner run = new QueryRunner();
        run.update(conn, "INSERT INTO roles (name,description,lft,rgt) values ('ROLE_BASE','ROLE_BASE',1,2)"); 
        DbUtils.commitAndClose(conn);

        Role parent = roleService.get("ROLE_BASE");
        roleService.add(new Role("ROLE_ADMIN","ROLE_ADMIN"),parent);
        roleService.add(new Role("ast-v-role1","ast-v-role1"),parent);
        roleService.add(new Role("ast-v-role3","ast-v-role3"),parent);
        parent = roleService.get("ast-v-role1");
        roleService.add(new Role("ast-v-role2","ast-v-role2"),parent);

        /* 将权限绑定到不同的角色
         * ROLE_BASE 
         *     |-----ROLE_ADMIN
         *     |
         *     |-----ast-v-role1 <==> (/ast-v-auth1.z ) (/ast-v-auth1.z?ci=addUser)
         *     |          |------ast-v-role2 
         *     |-----ast-v-role3 <==> (/ast-v-auth2.z )
         */
        Authority auth = authService.get("/ast-v-auth1.z");
        Role role = roleService.get("ast-v-role1");
        roleService.bind(role, auth);
        auth = authService.get("/ast-v-auth1.z?ci=addUser");
        roleService.bind(role, auth);
        auth = authService.get("/ast-v-auth2.z");
        role = roleService.get("ast-v-role3");
        roleService.bind(role, auth);

        /* 将用户绑定到ast-v-role2, admin 绑定到 ROLE_ADMIN
         * ROLE_BASE
         *     |-----ROLE_ADMIN <==> (admin)
         *     |
         *     |-----ast-v-role1
         *     |          |------ast-v-role2 <==> (ast-v-user1)
         *     |-----ast-v-role3 
         */
        role = roleService.get("ast-v-role2");
        User user = userService.get("ast-v-user1");
        roleService.bind(role,user);
        role = roleService.get("ROLE_ADMIN");
        user = userService.get("admin");
        roleService.bind(role,user);
    }

    @Test
    public void testGet() throws Exception {
        Authority auth = authService.get("/ast-v-auth1.z");
        String name = authService.get(auth.getId()).getName();
        Assert.assertTrue(auth.getName().equals(name));
    }

    @Test
    public void testUpdateAndDelete() throws Exception {
        authService.add(new Authority("sec.test.ast-v-auth4","/ast-v-auth4.z"));
        Authority auth = authService.get("/ast-v-auth4.z");
        Role role = roleService.get("ROLE_BASE");
        roleService.bind(role, auth);

        List<Authority> auths = authService.getAll();
	Cache cache = CacheUtils.getAuthCache();
        List<Authority> authsc = (List<Authority>)cache.get("authorities");
        Assert.assertNotNull(authsc);

        auth.setValue("/ast-v-auth4-none.z");
        authService.update(auth);
        Assert.assertNull(authService.get("/ast-v-auth4.z"));
        // 更新后 cache失效，等待下次加载
        authsc = (List<Authority>)cache.get("authorities");
        Assert.assertNull(authsc);
        auth = authService.get("/ast-v-auth4-none.z");
        Assert.assertNotNull(auth);
        roleService.unbind(role,auth);
        authService.delete(auth);
        Assert.assertNull(authService.get(auth.getId()));
    }

    @Test
    public void testFindByName() throws Exception {
        List<Authority> auths = authService.findByName("sec.ast-v-auth");
        Assert.assertTrue(auths.size()==3);
        auths = authService.findByName("sec.ast-v-auth1");
        Assert.assertTrue(auths.size()==1);
    }

    // 此用例覆盖了 RoleService.find(user) 方法。
    @Test
    public void testFindByUser() throws Exception {
        // 含继承关系
        User user = userService.get("ast-v-user1");
        List<Authority> auths = authService.find(user);
        Assert.assertTrue(auths.size()==2);
        // 不取继承关系的角色。
        List<Role> roles = roleService.find(user);
        Assert.assertTrue(roles.size()==1);
    }

    // 只显示直接绑定，不取继承关系。
    // 此用例覆盖了 UserService.find(Role) 方法。
    @Test
    public void testFindByRole() throws Exception {
        Role role = roleService.get("ast-v-role2");
        List<Authority> auths = authService.find(role);
        Assert.assertTrue(auths.size()==0);
        List<User> users = userService.find(role);
        Assert.assertTrue(users.size()==1);
    }

    // 此用例覆盖了 UserService.find(Authority) 和 RoleService.find(Authority) 方法。
    @Test
    public void testFindByAuth() throws Exception {
        // 不取继承关系的角色。
        Authority auth = authService.get("/ast-v-auth1.z");
        List<Role> roles = roleService.find(auth);
        Assert.assertTrue(roles.size()==1);
        // 含继承关系
        List<User> users = userService.find(auth);
        Assert.assertTrue(users.size()==1);
    }

    @Test
    public void testVerify() throws Exception {
        // 开始验证测试
        // ast-v-user1 由于角色继承，得到 /ast-v-auth1.z 的权限
        boolean bl = authService.verify("/ast-v-auth1.z","ast-v-user1");
        Assert.assertTrue(bl);
        // ast-v-user1 没有ast-v-auth2.z的权限，没有继承关系
        bl = authService.verify("/ast-v-auth2.z","ast-v-user1");
        Assert.assertTrue(!bl);
        // 访问一个存在，但没有做权限设置的auth，默认验证成功
        bl = authService.verify("/ast-v-auth3.z","ast-v-user1");
        Assert.assertTrue(bl);
        // 访问一个不存在的auth，验证失败
        bl = authService.verify("/ast-v-auth4.z","ast-v-user1");
        Assert.assertTrue(!bl);

        // ast-v-user1 有 特殊权限 /ast-v-auth1.z?ci=addUser
        bl = authService.verify("/ast-v-auth1.z?ci=addUser","ast-v-user1");
        Assert.assertTrue(bl);

        // 特殊角色ROLE_ADMIN下的用户 admin拥有所有权限，不判断继承关系
        bl = authService.verify("/ast-v-auth1.z","admin");
        Assert.assertTrue(bl);
        bl = authService.verify("/ast-v-auth3.z","admin");
        Assert.assertTrue(bl);
        bl = authService.verify("/ast-v-auth3.z","admin");
        Assert.assertTrue(bl);
        // ROLE_ADMIN 角色下的用户可以访问没有设置的权限
        bl = authService.verify("/ast-v-auth4.z","admin");
        Assert.assertTrue(bl);
    }

    @Test
    public void testFindMapByUsername() throws Exception {
        // 判断user 应该拥有一个权限/ast-v-auth1.z
        // 此外由于 /ast-v-auth3.z 没有做权限与角色的绑定，所以默认user也拥有此权限
        // map 中有 sec, sec.ast-v-auth1,sec.ast-v-auth3
        // 注意 map 不包括 0004 这个特殊权限。
        Map<String,String> map = authService.findMapByUsername("ast-v-user1");
        Assert.assertTrue(3==map.size());
        Assert.assertTrue("#".equals(map.get("sec")));
        Assert.assertTrue("/ast-v-auth1.z".equals(map.get("sec.ast-v-auth1")));
        Assert.assertNull(map.get("sec.ast-v-auth2"));
        // ROLE_ADMIN拥有所有权限，distinct后得到/ast-v-auth1.z 和/ast-v-auth2.z
        map = authService.findMapByUsername("admin");
        Assert.assertTrue(map.size()>=4);
        Assert.assertNotNull(map.get("sec.ast-v-auth2"));
    }

    // 重负荷测试，不用每次执行
    // @Test 
    public void testPerformance() throws Exception {
        // 增加 10 级角色，层层继承
        Role parent = roleService.get("ROLE_BASE");
        for (int i=0;i<10;i++) {
            Role role = new Role("ast-pf-role"+i,"ast-pf-role"+1);
            roleService.add(role,parent);
            parent = roleService.get(role.getName());
        }
        parent = roleService.get("ROLE_BASE");
        // 增加500个auth，算中间节点，菜单树超过1500个节点。
        // 全部绑定到ROLE_BASE
        // 全部占用160k cache
        for (int i=0;i<500;i++) {
            Authority auth = new Authority("lev1.lev"+i+".lev3.ast-pf-auth"+i,"/ast-pf-auth"+i+".z");
            authService.add(auth);
            auth = authService.get(auth.getValue());
            roleService.bind(parent, auth);
        }

        // 增加500个user，绑定到最底层的role
        Role role = roleService.get("ast-pf-role9");
        for (int i=0;i<500;i++) {
            User user = new User("ast-pf-user"+i,"ast-pf-user"+i,"123456");
            user.setEnabled(true);
            userService.add(user);
            user = userService.get(user.getUsername());
            roleService.bind(role,user);
        }

        // 取菜单时间，毫秒级，第一次没有Cache
        long start = System.currentTimeMillis();
        Map<String,String> map = authService.findMapByUsername("ast-pf-user100");
        System.out.println("no cache:" + (System.currentTimeMillis() - start) +" ms, get "+map.size()+" menus.");
        start = System.currentTimeMillis();
        map = authService.findMapByUsername("ast-pf-user100");
        System.out.println("in cache:" + (System.currentTimeMillis() - start) +" ms, get "+map.size()+" menus.");

        // 验证时间，纳秒级，一个user在cache大约占用0.4k
        start = System.nanoTime();
        boolean bl = authService.verify("/ast-pf-auth100.z","ast-pf-user101");
        System.out.println("no cache:" + (System.nanoTime() - start) +" ns, verify: "+bl);
        start = System.nanoTime();
        bl = authService.verify("/ast-pf-auth100.z","ast-pf-user101");
        System.out.println("in cache:" + (System.nanoTime() - start) +" ns, verify: "+bl);
    }

    @After
    public void tearDown() {
	clearTable();
    }
}
