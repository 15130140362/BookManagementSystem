package com.example.projectmanagement.tableEntity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class returnBookRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer one_book_id;
    private String bookName;
    private String returnBookDate;
    private String borrowDate;
    private String readerName;
    private String comment;
}
