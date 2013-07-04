package org.gaixie.jibu.security.model;

/**
 * 系统配置模型类。
 */
public class Setting implements Comparable {

    private Integer id;
    private String name;
    private String value;
    private Integer sortindex;
    private Boolean enabled;

    /**
     * No-arg constructor.
     */
    public Setting() {

    }

    /**
     * Full constructor
     */
    public Setting(String name,String value,Integer sortindex,Boolean enabled) {
        this.name = name;
        this.value = value;
        this.sortindex = sortindex;
        this.enabled = enabled;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Accessor Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Integer getSortindex() { return sortindex; }
    public void setSortindex(Integer sortindex) { this.sortindex = sortindex; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Setting)) return false;
        final Setting setting = (Setting) o;
        return getName().equals(setting.getName()) && getValue().equals(setting.getValue()) ;
    }

    public String toString() {
        return  "Setting (" + getId() + "), Name: '" + getName() + "', Value: '" + getValue() + "'";
    }

    public int compareTo(Object o) {
        if (o instanceof Setting) {
            final Setting setting = (Setting) o;
            int i = this.getName().compareTo(setting.getName());
            if (i==0){
                return this.getSortindex().compareTo(setting.getSortindex());
            }else
                return i;
        }
        return 0;
    }
}
