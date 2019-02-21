package com.example.projectmanagement.checkerTime;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Slf4j
public class reserveBookTimeChecker implements Runnable {
   /* static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }*/

    public Connection init() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/bookmanagement?useSSL=false&useUnicode=true&characterEncoding=utf-8" +
                "&allowMultiQueries=true&serverTimezone=UTC";
        return DriverManager.getConnection(url, "root", "15130140362");
    }

    @Override
    public void run() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = init();
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                log.info("***********************************");
                log.info("开始检查预约是否超旗");
                statement.executeUpdate("UPDATE book,(SELECT COUNT(*) as amount,book.id AS bkid\n" +
                        "from reserve_book,book,one_book\n" +
                        "WHERE reserve_book.reserv_time < \n" +
                        "DATE_SUB(NOW(),INTERVAL (SELECT sys_info_setting.info_value FROM sys_info_setting WHERE sys_info_setting.info_name='reserveHours') HOUR)" +
                        "AND reserve_book.book_id=one_book.id \n" +
                        "AND book.id=one_book.book_id\n" +
                        "AND reserve_book.is_vaild='true' GROUP BY book.id) AS temp2 \n" +
                        "SET book.sur_plus=book.sur_plus+temp2.amount WHERE temp2.bkid=book.id");
                statement.executeUpdate("UPDATE reserve_book,one_book \n" +
                        "SET reserve_book.is_vaild='false',one_book.book_state='AVAILABLE' \n" +
                        "WHERE reserve_book.book_id=one_book.id\n" +
                        "AND reserve_book.reserv_time < \n" +
                        "DATE_SUB(NOW(),INTERVAL (SELECT sys_info_setting.info_value FROM sys_info_setting WHERE sys_info_setting.info_name='reserveHours') HOUR)" +
                        "AND reserve_book.is_vaild='true'");
                //设置为一分钟循环一次
                log.info("开始睡眠十分钟");
                log.info("***********************************");
                Thread.sleep(1000 * 30);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new Thread(new reserveBookTimeChecker()).start();
    }

}
