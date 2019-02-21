package com.example.secauthorizelearn.tableEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SysRole {
    public SysRole() {
    }
    public SysRole(Long id) {
        this.id = id;
        this.name="ROLE_READER";
    }
    public SysRole(Long id, String roleName) {
        this.id = id;
        this.name = roleName;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
