package com.example.projectmanagement.sendEmail;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class test {
    public static int subTract(String dat1, String dat2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cl = Calendar.getInstance();
        cl.setTime(simpleDateFormat.parse(dat1));
        long dat1Mil = cl.getTimeInMillis();
        cl.setTime(simpleDateFormat.parse(dat2));
        long dat2Mil = cl.getTimeInMillis();
        long sub = (dat1Mil - dat2Mil) / (1000 * 3600 * 24);
        return (int) sub;
    }


    public static void downLoadPic(String Location) {
        try {
            URL url = new URL(Location);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream
                    (new File("F:\\idea2017.1.1代码\\images\\asdasdsad.jpg"));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length=dataInputStream.read(buffer))>0)
            {
                output.write(buffer,0,length);
            }
            fileOutputStream.write(output.toByteArray());
            fileOutputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws ParseException {
        downLoadPic("https://img3.doubanio.com/view/subject/s/public/s1627374.jpg");
    }
}
