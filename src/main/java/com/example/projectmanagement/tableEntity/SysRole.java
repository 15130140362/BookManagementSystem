package com.example.projectmanagement.tableEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class SysRole {
    public SysRole() {
    }
    public SysRole(Integer id) {
        this.id = id;
        this.name="ROLE_READER";
    }
    public SysRole(Integer id, String roleName) {
        this.id = id;
        this.name = roleName;
    }

    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "SysRole{" +
                "id='" + id + '\'' +
                ", roleName='" + name + '\'' +
                '}';
    }
}
