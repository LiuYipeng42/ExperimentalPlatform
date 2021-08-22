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

        HttpSession session = arg0.getSession();

        getAttributes(session);

        String userId = String.valueOf((long) session.getAttribute("userId"));
        long loginId = (long) session.getAttribute("loginId");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 10);

        studyRecordService.update(
                new UpdateWrapper<StudyRecord>()
                        .set("end_time", calendar.getTime())
                        .isNull("end_time")
                        .eq("student_id", userId)
                        .eq("login_id", loginId)
        );

        md5CollisionService.closeEnvironment(userId);
        paddingOracleService.closeEnvironment(userId);

        System.out.println("User " + userId + " logout!");
    }

    private void getAttributes(HttpSession session) {

        Enumeration<String> e = session.getAttributeNames();
        String key;
        while (e.hasMoreElements()) {
            key = e.nextElement();
            System.out.println(key + ": " + session.getAttribute(key));
        }

    }
}
