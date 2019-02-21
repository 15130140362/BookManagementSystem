package com.example.hibernatelearn;

import com.example.hibernatelearn.entity.Husband;
import com.example.hibernatelearn.entity.HusbandRepository;
import com.example.hibernatelearn.entity.Wife;
import com.example.hibernatelearn.entity.WifeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HibernatelearnApplicationTests {
    @Autowired
    HusbandRepository husbandRepository;
    @Autowired
    WifeRepository wifeRepository;

    @Test
    public void contextLoads() {
        Husband husband = new Husband("Jack");
        Wife wife = new Wife("Rose");
        husband.setWife(wife);
        wifeRepository.save(wife);
        husbandRepository.save(husband);
    }

    @Test
    public void addOtherOne() {
        Husband husband = new Husband("Milk");
        Wife w=wifeRepository.findByName("Rose");
        husband.setWife(w);
        husbandRepository.save(husband);
    }
    @Test
    public void addCascade() {
        Husband husband=new Husband("Tom");
        Wife wife=new Wife("Jerry");
        husband.setWife(wife);
        husbandRepository.save(husband);
    }
}
