package com.example.projectmanagement.tableEntity;

import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
public class borrowBookRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer bookId;//改为oneBookID
    private String  readerName;
    private String  borrowdate;
    private String  shouldReturnDate;
    //private Integer bookNum;
}
