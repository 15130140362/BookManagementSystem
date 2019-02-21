package com.example.hibernatelearn.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wife")
public class Wife {
    public Wife(){}

    public Wife(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wid;

    @Column(nullable = false)
    private String name;

    public Integer getWid() {
        return wid;
    }

    public void setWid(Integer wid) {
        this.wid = wid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode(){
        return wid;
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Wife))
            return  false;
        Wife wife = (Wife) obj;
        if(wife.getWid() == this.wid)
            return true;
        else
            return false;
    }

}
