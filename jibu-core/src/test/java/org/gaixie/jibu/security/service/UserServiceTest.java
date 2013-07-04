package org.gaixie.jibu.security.service;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.SecurityTestSupport;
import org.gaixie.jibu.security.model.Criteria;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserServiceTest extends SecurityTestSupport {
    private UserService userService;

    @Before
    public void setup() throws Exception {
	clearTable();
        userService = getInjector().getInstance(UserService.class);
        userService.add(new User("Administrator","admin","123456","admin@gaixie.org",1,true));
    }


    @Test
    public void testGet() throws Exception {
        User user = userService.get("admin");
        Assert.assertNotNull(user);
        user = userService.get(user.getId());
        Assert.assertNotNull(user);
        user = userService.get("notExistUser");
        Assert.assertNull(user);
    }

    @Test
    public void testAdd() throws Exception {
        User user = new User("tommy wang","testUserServiceAdd","123456","bitorb@gmail.com",1,true);
        userService.add(user);
        user = userService.get("testUserServiceAdd");
        Assert.assertNotNull(user);
    }

    @Test
    public void testUpdate() throws Exception {
        User user = userService.get("admin");
        user.setFullname("updateFullname");
        userService.update(user);
        user = userService.get("admin");
        Assert.assertTrue("updateFullname".equals(user.getFullname()));
    }

    @Test
    public void testDelete() throws Exception {
        User user = userService.get("admin");
        userService.delete(user);
        user = userService.get("admin");
        Assert.assertNull(user);
    }

    @Test
    public void testFind() throws Exception {
        User user = new User("tommy wang","testUserServiceAdd",
                             "123456","bitorb@gmail.com",1,true);
        userService.add(user);

        user = new User("tommy wang","testUserServiceAdd1",
                        "123456","bitorb@gmail.com",1,true);
        userService.add(user);

        user = new User("tommy wang","testUserServiceAdd2",
                        "123456","bitorb@gmail.com",0,true);
        userService.add(user);

        // 按用户全名查询
        user = new User();
        user.setFullname("tommy wang");
        List<User> users = userService.find(user);
        Assert.assertTrue(3 == users.size());

        // 按类型查询
        user = new User();
        user.setType(0);
        users = userService.find(user);
        Assert.assertTrue(1 == users.size());

        user.setType(1);
        users = userService.find(user);
        Assert.assertTrue(3 == users.size());

        Criteria crt = new Criteria();
        crt.setStart(0);
        crt.setLimit(1);
        crt.setSort("username");
        crt.setDir("DESC");

        // 没有 crt  select * from where enabled = true
        // 有 分页 crt  SELECT * FROM (
        //                            select ROW_NUMBER() OVER() AS R, userbase.*
        //                            from userbase) as TR
        //               where R >0 AND R <=1
        //               ORDER BY username DESC
        Assert.assertTrue(0 == crt.getTotal());
        users = userService.find(user,crt);
        Assert.assertTrue(1 == users.size());
        Assert.assertTrue("testUserServiceAdd1".equals(users.get(0).getUsername()));
        Assert.assertTrue(3 == crt.getTotal());
    }

    // 关于 UserService 的 find(Role),find(Authority) 方法在 AuthorityServiceTest中被测试。
    @After
    public void tearDown() {
	clearTable();
    }
}
