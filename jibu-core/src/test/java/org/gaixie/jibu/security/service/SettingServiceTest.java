package org.gaixie.jibu.security.service;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.SecurityTestSupport;
import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.SettingService;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SettingServiceTest extends SecurityTestSupport {
    private SettingService settingService;
    private UserService userService;

    @Before
    public void setup() throws Exception {
	clearTable();
        settingService = getInjector().getInstance(SettingService.class);
        userService = getInjector().getInstance(UserService.class);
        userService.add(new User("Administrator","admin","123456","jibu.gaixie@gmail.com",1,true));
        Setting setting = new Setting("theme","blue",0,true);
        settingService.add(setting);
        setting = new Setting("theme","gray",1,true);
        settingService.add(setting);
    }

    @Test
    public void testGet() throws Exception {
        Setting setting = settingService.get("theme","blue");
        Assert.assertNotNull(setting);
        Setting s = settingService.getDefault("theme");
        Assert.assertTrue(setting.equals(s));
        // test get(Integer) method
        s = settingService.get(setting.getId());
        Assert.assertTrue(setting.equals(s));
    }

    @Test
    public void testUpdateMe() throws Exception {
        User user = userService.get("admin");
        Setting setting = settingService.get("theme","gray");
        List<Integer> list = new ArrayList<Integer>();
        list.add(setting.getId());
        settingService.updateMe(list,user);
        setting = settingService.getByUsername("theme","admin");
        Assert.assertTrue("gray".equals(setting.getValue()));
        // 重置后
        settingService.reset("admin");
        setting = settingService.getByUsername("theme","admin");
        Assert.assertNull(setting);
        
    }

    @Test
    public void testUpdateOrDelete() throws Exception {
        Setting setting = settingService.get("theme","blue");
        setting.setValue("tommy");
        settingService.update(setting);
        setting = settingService.get("theme","tommy");
        Assert.assertNotNull(setting);
        settingService.delete(setting);
        setting = settingService.get("theme","tommy");
        Assert.assertNull(setting);
    }

    @Test
    public void testGetByUsername() throws Exception {
        Setting dfsetting = settingService.getDefault("theme");
        User user = userService.get("admin");
        Setting s = settingService.getByUsername("theme","admin");
        // 没有bind，所以取到 null
        Assert.assertNull(s);
        Setting setting = settingService.get("theme","gray");
        settingService.bind(setting.getId(),user.getUsername());
        s = settingService.getByUsername("theme","admin");
        // bind后，取绑定的 setting
        Assert.assertTrue(setting.equals(s));
        // 取消bind后，取到null
        settingService.unbind(setting.getId(),user.getUsername());
        s = settingService.getByUsername("theme","admin");
        Assert.assertNull(s);
    }

    @Test
    public void testFindByName() throws Exception {
        List<Setting> settings = settingService.findByName("theme");
        Assert.assertTrue(settings.size()==2);
    }

    @After
    public void tearDown() {
	clearTable();
    }
}
