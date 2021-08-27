package com.guet.ExperimentalPlatform.Listener;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.service.MD5CollisionService;
import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;


@Component
@WebListener
public class SessionListener implements HttpSessionListener {

    StudyRecordService studyRecordService;
    PaddingOracleService paddingOracleService;
    MD5CollisionService md5CollisionService;

    @Autowired
    public SessionListener(StudyRecordService studyRecordService,
                           PaddingOracleService paddingOracleService,
                           MD5CollisionService md5CollisionService) {
        this.studyRecordService = studyRecordService;
        this.paddingOracleService = paddingOracleService;
        this.md5CollisionService = md5CollisionService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        System.out.println("Session: " + arg0.getSession());
    }

    @SneakyThrows
    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {

        try {

            HttpSession session = arg0.getSession();

            getAttributes(session);

            long userId = (long) session.getAttribute("userId");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 30);

            studyRecordService.update(
                    new UpdateWrapper<StudyRecord>()
                            .set("end_time", new Date())
                            .eq("student_id", userId)
                            .isNull("end_time")
            );

            md5CollisionService.closeEnvironment(String.valueOf(userId));
            paddingOracleService.closeEnvironment(String.valueOf(userId));

            System.out.println("User " + userId + " logout!");
        } catch (Exception e){
            System.out.println("----------");
            e.printStackTrace();
            System.out.println("----------");

        }

    }

    private void getAttributes(HttpSession session) {

        Enumeration<String> e = session.getAttributeNames();
        String key;
        System.out.println("----------");
        while (e.hasMoreElements()) {
            key = e.nextElement();
            System.out.println(key + ": " + session.getAttribute(key));
        }
        System.out.println("----------");
    }
}
