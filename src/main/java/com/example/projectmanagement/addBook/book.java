package com.example.projectmanagement.addBook;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Data
@Entity
public class book implements Comparable<book>{
    public book() {
    }

    public book(String name, String author, String introduction, Integer numbers, String bookPicNumber,
                Integer surPlus, Integer bookTypeId, String bookLocation, String publisher, String  price) {
        this.name = name;
        this.author = author;
        this.introduction = introduction;
        this.numbers = numbers;
        this.bookPicNumber = bookPicNumber;
        this.surPlus = surPlus;
        this.bookTypeId = bookTypeId;
        this.bookLocation = bookLocation;
        this.publisher = publisher;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String author;
    private String introduction;
    private Integer numbers;
    private String bookPicNumber;
    private Integer surPlus;
    private Integer bookTypeId;
    private String bookLocation;
    private Integer similarityDegree;
    private String publisher;
    private String price;
    @Override
    public int compareTo(book o) {
        if(this.getSimilarityDegree()<=o.getSimilarityDegree())
        {
            return -1;
        }else
        {
            return 1;
        }
    }
}
