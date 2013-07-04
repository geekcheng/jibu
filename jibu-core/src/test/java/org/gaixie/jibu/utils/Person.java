package org.gaixie.jibu.utils;

import java.sql.Timestamp;
import java.util.Date;
/*
 * 此Bean只用于BeanConverter的测试
 */ 
public class Person {
    
    private Integer id;
    private String name;
    private int age;
    private boolean married = false;
    private Boolean hasChildren;
    private float salary;
    private Date birthday;
    private Timestamp deathtime;
    public Person() {
    }

    public Person(String name,int age,boolean married) {
        this.name = name;
        this.age = age;  
        this.married = married;  
    }
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public void setMarried(boolean married) { this.married = married; }
    public boolean isMarried() { return married; }

    public void setHasChildren(Boolean hasChildren) { this.hasChildren = hasChildren; }
    public Boolean getHasChildren() { return hasChildren; }

    public float getSalary() { return salary; }
    public void setSalary(float salary) { this.salary = salary; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public Timestamp getDeathtime() { return deathtime; }
    public void setDeathtime(Timestamp deathtime) { this.deathtime = deathtime; }
}
