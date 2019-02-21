package com.example.projectmanagement.tableEntity;


import com.example.projectmanagement.checkerTime.overDueTimeChecker;
import com.example.projectmanagement.checkerTime.reserveBookTimeChecker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
@Component
public class ThreadBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        //检查预约图书是否超旗
        //检查预约是否成功
       // Thread reserveBookThread = new Thread(new reserveBookTimeChecker());
        //reserveBookThread.start();
        //图书是否超期检查成功
        //Thread bookOverDueThread = new Thread(new overDueTimeChecker());
        //bookOverDueThread.start();
    }
}
