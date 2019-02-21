package com.example.projectmanagement.tableEntity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class reserveBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer bookId;//id为oneBook的Id
    private String username;
    private String reservTime;
   /* private String endTIme;*/
    private String isVaild;
}
