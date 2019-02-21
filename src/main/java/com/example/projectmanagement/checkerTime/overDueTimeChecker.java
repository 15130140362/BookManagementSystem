package com.example.projectmanagement.checkerTime;


import com.example.projectmanagement.sendEmail.SendMail;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


@Slf4j
public class overDueTimeChecker implements Runnable {

    private Connection connection;
    private String url;
    private Statement statement;
    private static Map<Integer, String> timeReminderMap = new HashMap<>();
    /*static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }*/

    public void init() {
        url = "jdbc:mysql://127.0.0.1:3306/bookmanagement?useSSL=false&useUnicode=true&characterEncoding=utf-8" +
                "&allowMultiQueries=true&serverTimezone=UTC";
        try {
            connection = DriverManager.getConnection(url, "root", "15130140362");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        init();
        while (true) {
        /*    log.info("开始检查是否有图书超期");*/
            ResultSet resultSet =
                    null, resultSet1 = null;
            try {

                resultSet = statement.executeQuery("SELECT borrow_book_record.should_return_date,\n" +
                        "borrow_book_record.reader_name,\n" +
                        "book.`name`,\n" +
                        "one_book.id,\n" +
                        "reader_email_number.email\n" +
                        "FROM borrow_book_record,sys_user,reader_email_number,one_book,book\n" +
                        "WHERE should_return_date<=DATE_SUB(CURDATE(),\n" +
                        "INTERVAL -(SELECT sys_info_setting.info_value FROM sys_info_setting WHERE sys_info_setting.info_name='notifyMinusDays') DAY) \n" +
                        "AND should_return_date>=CURDATE()\n" +
                        "AND reader_email_number.userid=sys_user.id\n" +
                        "AND sys_user.username=borrow_book_record.reader_name\n" +
                        "AND one_book.id=borrow_book_record.book_id\n" +
                        "AND book.id=one_book.book_id");
                while (resultSet.next()) {
                    String reader_name = resultSet.getString("reader_name");
                    String should_return_date = resultSet.getString("should_return_date");
                    String bookName = resultSet.getString("name");
                    String oneBookId = resultSet.getString("id");
                    String email = resultSet.getString("email");
                    int days = getDifferenceDayBetweenThisTwoDay(should_return_date, currentTime());
                    String val = "Reader " + reader_name + " ," +
                            " Please Return The Book " + bookName + " In Time Book Id is " + oneBookId + " , This Book Will Overdue After " + days + " days," +
                            "You Should Return Before " + should_return_date + " ,Otherwise , You Must Pay Fine.";
                    email = email.trim();
                    if (timeReminderMap.containsKey(Integer.parseInt(oneBookId))) {
                        String time = timeReminderMap.get(Integer.parseInt(oneBookId));
                        if (!time.equals(currentTime())) {
                            timeReminderMap.put(Integer.parseInt(oneBookId), currentTime());
                            log.info("不是这天发送的，再次发送");
                            log.info("****************************************");
                            log.info("提醒信息：" + val);
                            log.info("邮箱是：" + email);
                            log.info("****************************************");
                             new Thread(new SendMail(email, val)).start();
                        }else{
                          /*  log.info("是这天发送的，今天之内不再次发送了。");*/
                        }
                    } else {
                        timeReminderMap.put(Integer.parseInt(oneBookId), currentTime());
                        log.info("发送邮箱");
                        log.info("****************************************");
                        log.info("提醒信息：" + val);
                        log.info("邮箱是：" + email);
                        log.info("****************************************");
                         new Thread(new SendMail(email, val)).start();
                    }
                }
            } catch (SQLException e) {
                log.info("图书超期提醒的时候检查图书是否超期限 出现异常");
                e.printStackTrace();
            } catch (ParseException e) {
                log.info("图书超期提醒的时候检查图书是否超期限 出现异常");
                e.printStackTrace();
            }
            try {
                System.out.println("开始睡眠30秒。。。");
                Thread.sleep(1000 * 30);
                //  Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDifferenceDayBetweenThisTwoDay(String dat1, String dat2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cl = Calendar.getInstance();
        cl.setTime(simpleDateFormat.parse(dat1));
        long dat1Mil = cl.getTimeInMillis();
        cl.setTime(simpleDateFormat.parse(dat2));
        long dat2Mil = cl.getTimeInMillis();
        long days = (dat1Mil - dat2Mil) / (1000 * 3600 * 24);
        return (int) days;
    }

    public String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }


    public static void main(String[] args) {
        new Thread(new overDueTimeChecker()).start();
    }
}
