package com.example.projectmanagement.sendEmail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;


public class SendMail extends Thread {
    private String email;
    private String content;

    public SendMail(String email, String content) {
        this.email = email;
        this.content = content;
    }

    public boolean sendEmail(String email, String content) {
        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName("smtp.163.com");
      //  htmlEmail.setHostName("smtp.sohu.com");
        htmlEmail.setCharset("utf-8");
        try {
            htmlEmail.addTo(email);
              htmlEmail.setFrom("18792962699@163.com", "Bibliosoft");
              htmlEmail.setAuthentication("18792962699@163.com", "sxuyan920127");
          //  htmlEmail.setAuthentication("field_learn@sohu.com", "sxuyan920127");
          //  htmlEmail.setAuthentication("18009235523@163.com", "sxuyan920127");
            htmlEmail.setMsg(content);
            htmlEmail.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void run() {
        sendEmail(email, content);
    }

    public static void main(String[] args) {
        new Thread(new SendMail("field_learn@qq.com","你好")).start();
    }
}

