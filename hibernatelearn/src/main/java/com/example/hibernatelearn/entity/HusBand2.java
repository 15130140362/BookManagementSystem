package com.example.hibernatelearn.entity;

import javax.persistence.*;

@Entity
@Table(name = "hs2")
public class HusBand2 {
    @Id
    @OneToOne
    @PrimaryKeyJoinColumn(name="h_id2",referencedColumnName ="wid" )
    private Wife wife;
    ///如何设置一个表的主键是另一个表的外键
}
