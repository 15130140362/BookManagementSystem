package com.example.projectmanagement;

import com.example.projectmanagement.addBook.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.Max;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectmanagementApplicationTests {


    @Autowired
    bookShelfRepository bookShelfRepository;

    @Test
    public void add() {
        for (int i = 0; i < 150; i++) {
            bookShelf bk = new bookShelf();
            bk.setType(-1);
            bk.setSum(0);
            bk.setMax(200);
            bk.setId(i);
            bookShelfRepository.save(bk);
        }
    }


    public void allocateBookShelf() {
        //最多只能增加十个书架
        int typeId = 10;
        int MaxShelfPerarea = 10;
        int needShelf = 3;
        List<Integer> resultList = bookShelfRepository.findValidBookShelf();
        //每个区10个书架
        for (int i = 0; i < resultList.size() - needShelf + 1; i++) {
            if (resultList.get(i) / MaxShelfPerarea == resultList.get(i + needShelf - 1) / MaxShelfPerarea && resultList.get(i + needShelf - 1) - resultList.get(i) == needShelf - 1) {
                bookShelfRepository.updateBookType(typeId, resultList.get(i), resultList.get(i + needShelf - 1));
                break;
            }
        }
    }


    @Test
    public void getLocation() {
        int typeID = 11;
        int bookAmount = 10;
        List<bookShelf> bookShelfList = bookShelfRepository.findByType(typeID);
        for (int i = 0; i < bookShelfList.size(); i++) {
            bookShelf bkS = bookShelfList.get(i);
            if (bkS.getSum() + bookAmount > bkS.getMax()) {
                System.out.println("放满了");
                continue;
            }
            bkS.setSum(bkS.getSum()+bookAmount);
            bookShelfRepository.save(bkS);
            String floor=bkS.getId()/50+1+" floor ";
            String area=bkS.getId()%50/10+1+" area ";
            String shelf=bkS.getId()%10+1+" shelf ";
            System.out.println(floor+area+shelf);
            return;
        }

    }
}
