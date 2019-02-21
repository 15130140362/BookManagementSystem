package com.example.projectmanagement.addBook;


import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name="bookType")
public class bookType {
    public bookType() {
    }

    public bookType(String typename) {
        this.typename = typename;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String typename;
}
