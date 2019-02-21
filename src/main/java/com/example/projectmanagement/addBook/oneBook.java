package com.example.projectmanagement.addBook;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
public class oneBook {

    public oneBook() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer bookId;


    private String bookState;

    private Integer tradeId;//记录的
}
