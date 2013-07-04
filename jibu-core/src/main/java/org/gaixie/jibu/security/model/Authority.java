package org.gaixie.jibu.security.model;

import java.io.Serializable;

/**
 * 权限资源模型类。实现 Serializable 接口，使其可以序列化到 Cache 中。
 * <p>
 */
public class Authority implements Serializable {
    
    private Integer id;
    private String name;
    private String value;
    private String display;

    /**
     * No-arg constructor.
     */
    public Authority() {
        
    }

    /**
     * Simple constructor
     */
    public Authority(String name,String value) {
        this.name = name;
        this.value = value;
     }
    

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplay() { return display; }
    public void setDisplay(String display) { this.display = display; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
