package com.example.projectmanagement.tableEntity;


import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
public class fine implements Comparable<fine>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer fineValue;//缴纳的罚金值
    private String username;
    private String borrowDate;
    private String shouldReturnDate;
    private String returnDate;
    private String bookName;
    private Integer bookId;

    @Override
    public int compareTo(fine o) {
        try {
            return compare_date(this.returnDate,o.getReturnDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public  int compare_date(String DATE1, String DATE2) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = df.parse(DATE1);
        Date dt2 = df.parse(DATE2);
        if (dt1.getTime() > dt2.getTime()) {
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
