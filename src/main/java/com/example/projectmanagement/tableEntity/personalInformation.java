package com.example.projectmanagement.tableEntity;


import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
public class personalInformation {

    public personalInformation() {
    }

    public personalInformation(String name, String position,
                               String sex, String birthday, String about, Integer userid) {
        this.name = name;
        this.position = position;
        this.sex = sex;
        this.birthday = birthday;
        this.about = about;

        this.userid = userid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String position;
    private String sex;
    private String birthday;
    private String about;
    private Integer userid;

    public void setNull(){
        this.name="";
        this.position="";
        this.sex="";
        this.birthday="";
        this.about="";
    }

    public static void main(String[] args) {
        System.out.println(new personalInformation().toString());
    }
}
