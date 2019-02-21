package com.example.projectmanagement.addBook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    final int Max_Floor = 3;
    final int Max_Area_perF = 5;
    final int Max_Shelf_perA = 10;
    //final int Max_Layer_perS = 4;
    //final int Max_Book_perL = 50;

    public class shelf {
        int id;
        int empty;
        int type;
        int Max = 200;
        int sum;

        public int getid() {
            return id;
        }

        public int gettype() {
            return type;
        }

        public int getsum() {
            return sum;
        }

        public void setsum(int sum) {
            this.sum = sum;
        }

        public int getMax() {
            return Max;
        }
    }

    public int addType(String typename, int needshelf) {

        //TODO addType to数据库 并get到它的id
        int typeId = 000;

        //TODO select shelf表 where empty = 1
        List<shelf> ll = null;

        for (int i = 0; i < Max_Floor * Max_Area_perF * Max_Shelf_perA - needshelf; i++) {
            if (ll.get(i).getid() / Max_Shelf_perA == ll.get(i + needshelf - 1).getid() / Max_Shelf_perA) {
                for (int j = i; j < needshelf; j++) {
                    //TODO 把shelf表中的empty=0，type=typeId ; where id=ll.get(j).getid()
                }
                return i;
            }
        }
        return 0;
    }

    public String getBooklocation(int bookid, int booktype) {
        //TODO select shelf表 where type = booktype
        List<shelf> ll = null;

        for (int i = 0; ; i++) {
            if (ll.get(i) == null) {
                return "书架放满啦";
            }
            shelf sh = ll.get(i);
            if (sh.getsum() >= sh.getMax()) {
                continue;
            }
            sh.setsum(sh.getsum() + 1);
            StringBuilder sb = new StringBuilder();
            sb.append((sh.getid() / Max_Shelf_perA + 1) / Max_Area_perF + 1).append("floor");
            sb.append((sh.getid() / Max_Shelf_perA + 1) % Max_Area_perF).append("area");
            sb.append(sh.getid() % Max_Shelf_perA).append("shelf");
            return sb.toString();
        }
    }


    public static int compare_date(String DATE1, String DATE2) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
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


    public static String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }


    public static String deleteDoubleAndOne(String str) {
        String regexp = "\'";
        String regxp2="\"";
        str = str.replaceAll(regexp, "");
        str=str.replaceAll(regxp2,"");
        return str;
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(deleteDoubleAndOne("asd'da\"s\"d"));
    }
  /*  public static String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
    public static String reserveEndTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.HOUR, 2);
        date1 = calendar.getTime();
        return sdf.format(date1);
    }
    public static List<String> weekList(String  str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date=simpleDateFormat.parse(str);
        List<String> weeklist = new LinkedList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        for(int i=0; i<7; i++ ){
            Date date1 = new Date(calendar.getTimeInMillis());
            weeklist.add(simpleDateFormat.format(date1));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return weeklist;
    }*/

}
