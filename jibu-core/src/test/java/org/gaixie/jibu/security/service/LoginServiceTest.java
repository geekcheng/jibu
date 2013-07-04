package org.gaixie.jibu.security.service;

import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.SecurityTestSupport;
import org.gaixie.jibu.security.model.Token;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.LoginException;
import org.gaixie.jibu.security.service.LoginService;
import org.gaixie.jibu.security.service.UserService;
import org.gaixie.jibu.utils.ConnectionUtils;
import org.gaixie.jibu.utils.SQLBuilder;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LoginServiceTest extends SecurityTestSupport {
    private LoginService loginService;
    private UserService userService;

    @Before
    public void setup() throws Exception {
	clearTable();
        loginService = getInjector().getInstance(LoginService.class);
        userService = getInjector().getInstance(UserService.class);
        userService.add(new User("Administrator","admin","123456","jibu.gaixie@gmail.com",1,true));
    }


    @Test
    public void testLoginSuccess() throws LoginException {
        loginService.login("admin", "123456");
    }

    @Test (expected = LoginException.class)
    public void testLoginFailed() throws LoginException {
        loginService.login("admin", "654321");
    }

    @Test
    public void testGenerateToken() throws JibuException {
        Token token = loginService.generateToken("wrong-username");
        Assert.assertNull(token);
        token = loginService.generateToken("admin");
        Assert.assertNotNull(token);
    }

    @Test
    public void testResetPassword() throws JibuException {
        Token token = loginService.generateToken("admin");
        Assert.assertNotNull(token);

        // 成功的修改密码，由 123456 改为 654321
        loginService.resetPassword(token.getValue(),"654321");
        loginService.login("admin","654321");
    }

    @Test (expected = TokenException.class)
    public void testTokenExpired() throws Exception {
        Token token = loginService.generateToken("admin");
        Assert.assertNotNull(token);

        // 修改token的有效期(设置为当前时间)，让token过期。
        long time = Calendar.getInstance().getTimeInMillis();
        Timestamp ts = new Timestamp(time);

        Token tk = new Token();
        tk.setExpiration(ts);
        Connection conn = ConnectionUtils.getConnection();
        QueryRunner run = new QueryRunner();
        String s = SQLBuilder.beanToSQLClause(tk,",");
        String sql = "Update tokens "+SQLBuilder.getSetClause(s)+" where value = '"+token.getValue()+"'";
        run.update(conn, sql);
        DbUtils.commitAndClose(conn);

        loginService.resetPassword(token.getValue(),"654321");
    }

    @Test (expected = TokenException.class)
    public void testTokenInvalid() throws Exception {
        Token token = loginService.generateToken("admin");
        Assert.assertNotNull(token);
        loginService.resetPassword("aaaaa","654321");
    }

    @Test (expected = JibuException.class)
    public void testTokenPasswordInvalid() throws Exception {
        Token token = loginService.generateToken("admin");
        Assert.assertNotNull(token);
        loginService.resetPassword(token.getValue(),"");
    }

    @After
    public void tearDown() {
	clearTable();
    }
}
