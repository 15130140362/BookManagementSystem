package com.example.projectmanagement.tableEntity;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Data
@Entity
public class announcementMessage implements Comparable<announcementMessage> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String body;
    private String messageType;
    private String date;
    @Override
    public int compareTo(announcementMessage o) {
        int temp = 0;
        try {
            temp = compareTwoDays(this.date, o.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(temp<0)
        {
            return 1;
        }else{
            return -1;
        }
    }

    private String name;

    public int compareTwoDays(String dat1, String dat2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cl = Calendar.getInstance();
        cl.setTime(simpleDateFormat.parse(dat1));
        long dat1Mil = cl.getTimeInMillis();
        cl.setTime(simpleDateFormat.parse(dat2));
        long dat2Mil = cl.getTimeInMillis();
        if (dat1Mil - dat2Mil >= 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
