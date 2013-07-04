package org.gaixie.jibu.security.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.gaixie.jibu.security.dao.TokenDAO;
import org.gaixie.jibu.security.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Token 数据访问接口的 PostgreSQL 实现。
 * <p>
 */
public class TokenDAOPgSQL implements TokenDAO {
    private static final Logger logger = LoggerFactory.getLogger(TokenDAOPgSQL.class);
    private QueryRunner run = null;

    public TokenDAOPgSQL() {
	this.run = new QueryRunner();
    }

    public Token get(Connection conn, int id) throws SQLException {
        ResultSetHandler<Token> h = new BeanHandler(Token.class);
        return run.query(conn
                         , "SELECT id,value,type,expiration,user_id FROM tokens WHERE id=? "
                         , h
                         , id);
    }

    public Token get(Connection conn, String value) throws SQLException {
        ResultSetHandler<Token> h = new BeanHandler(Token.class);
        return run.query(conn
                         , "SELECT id,value,type,expiration,user_id FROM tokens WHERE value=? "
                         , h
                         , value);
    }

    public void save(Connection conn, Token token) throws SQLException {
        run.update(conn
                   , "INSERT INTO tokens (value,type,expiration,user_id) VALUES (?,?,?,?)"
                   , token.getValue()
                   , token.getType()
                   , token.getExpiration()
                   , token.getUser_id());
    }

    /**
     * {@inheritDoc}
     * <p>
     * token.getId() 不能为 null。
     */
    public void delete(Connection conn, Token token) throws SQLException {
        run.update(conn
                   , "DELETE FROM tokens WHERE id=?"
                   , token.getId());
    }
}
