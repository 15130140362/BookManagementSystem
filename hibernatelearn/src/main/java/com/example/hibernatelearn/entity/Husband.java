package com.example.hibernatelearn.entity;

import javax.persistence.*;

@Entity
public class Husband {

    public Husband(){}

    public Husband(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hid;

    @Column(nullable = false)
    private String name;

    //OneToOne对于关系维护端来说的，一个Husband，最多对应一个wife,一个wife可以有多个husband
    //单向的OneToOne
    //@OneToOne(targetEntity = Wife.class)
    @OneToOne(targetEntity = Wife.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "wife_id",referencedColumnName = "wid")
    private Wife wife;

    public Integer getHid() {
        return hid;
    }

    public void setHid(Integer hid) {
        this.hid = hid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Wife getWife() {
        return wife;
    }

    public void setWife(Wife wife) {
        this.wife = wife;
    }
}
