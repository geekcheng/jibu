package org.gaixie.jibu.security.model;

import java.sql.Timestamp;

/**
 * 令牌模型类。
 */
public class Token {

    private Integer id;
    private String value;
    private String type;
    private Timestamp expiration;
    private Integer user_id;

    /**
     * No-arg constructor.
     */
    public Token() {

    }

    /**
     * Full constructor
     */
    public Token(String value,String type,Timestamp expiration,Integer user_id) {
        this.value = value;
        this.type = type;
        this.expiration = expiration;
        this.user_id = user_id;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Timestamp getExpiration() { return expiration; }
    public void setExpiration(Timestamp expiration) { this.expiration = expiration; }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        final Token token = (Token) o;
        return getValue() == token.getValue();
    }
}
