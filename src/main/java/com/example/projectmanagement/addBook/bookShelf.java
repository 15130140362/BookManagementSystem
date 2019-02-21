package com.example.projectmanagement.addBook;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
//一共三层，每层5个区，每个区10个书架
public class bookShelf {

    public bookShelf() {
    }

    public bookShelf(Integer id, int type) {
        this.id = id;
        this.type = type;
    }

    @Id
    private Integer id;
    private int type;
    private int Max = 200;//一个书架最多存多少本书
    private int sum; //当前的书架上面的总容量
}
