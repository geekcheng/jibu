package org.gaixie.jibu.security.model;

/**
 * 继承型角色模型类。
 * <p>
 */
public class Role {
    
    private Integer id;
    private String name;
    private String description;
    private Integer lft;
    private Integer rgt;
    private Integer depth;

    /**
     * No-arg constructor.
     */
    public Role() {
    }

    /**
     * Simple constructor
     */
    public Role(String name,String description) {
        this.name = name;
        this.description = description;
     }
    

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getLft() { return lft; }
    public void setLft(Integer lft) { this.lft = lft; }

    public Integer getRgt() { return rgt; }
    public void setRgt(Integer rgt) { this.rgt = rgt; }

    public Integer getDepth() { return depth; }
    public void setDepth(Integer depth) { this.depth = depth; }

    // ********************** Common Methods ********************** //
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        final Role role = (Role) o;
        return getName().equals(role.getName());
    }
}
