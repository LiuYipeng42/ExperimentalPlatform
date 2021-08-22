package com.guet.ExperimentalPlatform.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;


@RestController
public class TimerController {

    private final StudyRecordService studyRecordService;

    private static final HashMap<String, String> pageType = new HashMap<>();

    static {
        pageType.put("fileTransmission", "1");
        pageType.put("paddingOracle", "2");
        pageType.put("md5Collision", "3");
        pageType.put("rsa", "4");
        pageType.put("hash", "5");
        pageType.put("aes", "6");
        pageType.put("aesProcedure", "7");
        pageType.put("aesAvalanche", "8");
        pageType.put("rsaCoding", "9");
        pageType.put("hashCoding", "10");
        pageType.put("aesCoding", "11");
    }

    @Autowired
    public TimerController(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    @GetMapping("/heartbeat")
    public void heartbeat(HttpServletRequest request,
                          @RequestParam("page") String nextPage) {

        HttpSession session = request.getSession();

        String presentPage = (String) session.getAttribute("page");

        long userId;
        long loginId;
        String presentPageRecordId;
        String nextPageRecordId;

        if (!nextPage.equals(presentPage)) {
            // 页面发生切换

            userId = (long) session.getAttribute("userId");

            loginId = (long) session.getAttribute("loginId");

            presentPageRecordId = (String) session.getAttribute(presentPage + "RecordId");

            nextPageRecordId = (String) session.getAttribute(nextPage + "RecordId");

            if (presentPage != null) {
                // 若访问过一个页面，则加上上一个页面的结束时间
                studyRecordService.update(
                        new UpdateWrapper<StudyRecord>()
                                .set("end_time", new Date())
                                .eq("id", presentPageRecordId)
                                .eq("student_id", userId)
                                .eq("login_id", loginId)
                                .eq("experiment_type", pageType.get(presentPage))
                );
            }

            session.setAttribute("page", nextPage);

            if (nextPageRecordId == null) {
                // 以前从没有访问过这个页面，插入访问数据
                StudyRecord studyRecord = new StudyRecord()
                        .setStudentId(userId)
                        .setLoginId(loginId)
                        .setStartTime(new Date())
                        .setExperimentType(Integer.parseInt(pageType.get(nextPage)));

                studyRecordService.save(studyRecord);

                session.setAttribute(nextPage + "RecordId", String.valueOf(studyRecord.getId()));

            }

        }

    }
}
