package org.gaixie.jibu.security.model;

/**
 * 用户模型类。
 * <p>
 */
public class User {
    
    private Integer id;
    private String fullname;
    private String username;
    private String password;
    private Integer type;
    private String emailaddress;
    private Boolean enabled;
    
    /**
     * No-arg constructor.
     */
    public User() {
        
    }

    /**
     * Simple constructor.
     */
    public User(String fullname,String username,String password) {
        this.fullname = fullname;
        this.username = username;     
        this.password = password;  
    }
    
    /**
     * Full constructor.
     */
    public User(String fullname,String username,String password,String emailaddress,Integer type,boolean enabled) {
        this.fullname = fullname;
        this.username = username;     
        this.password = password;  
        this.emailaddress = emailaddress; 
        this.type = type; 
        this.enabled = enabled;  
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmailaddress() { return emailaddress; }
    public void setEmailaddress(String emailaddress) { this.emailaddress = emailaddress; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public Boolean getEnabled() { return enabled; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        final User user = (User) o;
        return getUsername().equals(user.getUsername());
    }

    public int hashCode() {
        return getUsername().hashCode();
    }
}
