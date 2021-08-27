package com.guet.ExperimentalPlatform.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guet.ExperimentalPlatform.entity.*;
import com.guet.ExperimentalPlatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class ReportInformationController {

    private final StudentService studentService;

    private final AlgorithmRecordService algorithmRecordService;

    private final StudyRecordService studyRecordService;

    private final MD5CollisionService md5CollisionService;

    private final PORunCodesRecordService poRunCodesRecordService;

    private final CodeTestRecordService codeTestRecordService;

    private final FileTransmissionService fileTransmissionService;

    @Autowired
    public ReportInformationController(
            StudentService studentService,
            AlgorithmRecordService algorithmRecordService,
            StudyRecordService studyRecordService,
            MD5CollisionService md5CollisionService,
            PORunCodesRecordService poRunCodesRecordService,
            CodeTestRecordService codeTestRecordService,
            FileTransmissionService fileTransmissionService) {
        this.studentService = studentService;
        this.algorithmRecordService = algorithmRecordService;
        this.studyRecordService = studyRecordService;
        this.md5CollisionService = md5CollisionService;
        this.poRunCodesRecordService = poRunCodesRecordService;
        this.codeTestRecordService = codeTestRecordService;
        this.fileTransmissionService = fileTransmissionService;
    }

    @GetMapping("report")
    public String getReport(@RequestParam("userAccount") String userAccount) {

        long userId = studentService.getOne(new QueryWrapper<Student>().eq("account", userAccount)).getId();

        double algorithmScore;
        double algorithmAttackScore;
        double digitalEnvelopeScore;

        HashMap<Integer, Double> studyTime = new HashMap<>();

        studyTime.put(1, 0.0);
        studyTime.put(2, 0.0);
        studyTime.put(3, 0.0);
        studyTime.put(4, 0.0);
        studyTime.put(5, 0.0);
        studyTime.put(6, 0.0);
        studyTime.put(7, 0.0);

        List<StudyRecord> studyRecords = studyRecordService.list(new QueryWrapper<StudyRecord>().eq("student_id", userId));

        for (StudyRecord s : studyRecords) {
            studyTime.put(
                    s.getExperimentType(),
                    studyTime.get(s.getExperimentType()) + (s.getEndTime().getTime() - s.getStartTime().getTime()) / 60000.0
            );
        }

        // --------------------------------------------------------------------------------------
        // 运行分数 65 分
        algorithmScore = (algorithmRecordService.list(
                new QueryWrapper<AlgorithmRecord>().eq("student_id", userId)
        ).size() / 12.0) * 65;

        // rsa 10分钟 10分
        if (studyTime.get(4) < 10) {
            algorithmScore += studyTime.get(4);
        } else {
            algorithmScore += 10;
        }

        // hash 10分钟 10分
        if (studyTime.get(5) < 10) {
            algorithmScore += studyTime.get(5);
        } else {
            algorithmScore += 10;
        }

        // aes 10分钟 15分
        if (studyTime.get(6) < 10) {
            algorithmScore += studyTime.get(6) * 1.5;
        } else {
            algorithmScore += 15;
        }

        // --------------------------------------------------------------------------------------
        // --------------------------------------------------------------------------------------

        double md5CollisionScore;
        List<MD5TaskRecord> md5TaskRecords = md5CollisionService.list(
                new QueryWrapper<MD5TaskRecord>().eq("student_id", userId).eq("status", "finished")
        );

        HashMap<String, Double> md5TaskTime = new HashMap<>();

        for (MD5TaskRecord record : md5TaskRecords) {
            md5TaskTime.put(
                    record.getTaskName(),
                    (record.getEndTime().getTime() - record.getStartTime().getTime()) / 60000.0
            );
        }

        // 四个任务一个 20 分，共 80 分
        md5CollisionScore = (md5TaskRecords.size() / 4.0) * 80;

        // 20 分钟 20分
        if (studyTime.get(3) < 20) {
            md5CollisionScore += studyTime.get(3);
        } else {
            md5CollisionScore += 20;
        }

        // --------------------------------------------------------------------------------------
        double paddingOracleScore;
        // 完成 auto attack 60 分
        if (poRunCodesRecordService.list(
                new QueryWrapper<PORunCodesRecord>()
                        .eq("student_id", userId)
                        .eq("code_type", "auto_attack")
                        .eq("status", "success")
        ).size() > 0) {
            paddingOracleScore = 60;
        } else {
            paddingOracleScore = 0;
        }

        // 运行 一次 manual attack 20 分
        if (poRunCodesRecordService.list(
                new QueryWrapper<PORunCodesRecord>()
                        .eq("student_id", userId)
                        .eq("code_type", "manual_attack")
        ).size() > 0) {
            paddingOracleScore += 20;
        }

        // 10 分钟 20 分
        if (studyTime.get(2) < 10) {
            paddingOracleScore += studyTime.get(3) * 2;
        } else {
            paddingOracleScore += 20;
        }

        algorithmAttackScore = 0.5 * md5CollisionScore + 0.5 * paddingOracleScore;

        // --------------------------------------------------------------------------------------
        // --------------------------------------------------------------------------------------

        // 代码考核 50分
        List<CodeTestRecord> codeTestRecords = codeTestRecordService.list(
                new QueryWrapper<CodeTestRecord>().eq("student_id", userId)
        );

        HashMap<String, Double> codeTestTime = new HashMap<>();

        for (CodeTestRecord record : codeTestRecords) {
            codeTestTime.put(
                    record.getCodeType(),
                    (record.getEndTime().getTime() - record.getStartTime().getTime()) / 60000.0
            );
        }

        digitalEnvelopeScore = (codeTestRecords.size() / 6.0) * 50;

        boolean sender = false;
        boolean receiver = false;

        for (FTAllInfo info : fileTransmissionService.getAllInfo(userId)) {

            if (info.getSenderId() == userId) {
                sender = true;
            }

            if (info.getReceiverId() == userId) {
                receiver = true;
            }
        }

        // 两次传输，一次发送方 25分，一次接收方 25分
        if (sender) {
            digitalEnvelopeScore += 25;
        }

        if (receiver) {
            digitalEnvelopeScore += 25;
        }

        // --------------------------------------------------------------------------------------

        System.out.println(algorithmScore);

        System.out.println(algorithmAttackScore);

        System.out.println(digitalEnvelopeScore);

        md5TaskTime.forEach((k, v) -> System.out.println(k + " " + v));

        codeTestTime.forEach((k, v) -> System.out.println(k + " " + v));

        studyTime.forEach((k, v) -> System.out.println(k + " " + v));

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("算法基础", algorithmScore);
        jsonObject.put("算法攻击", algorithmAttackScore);
        jsonObject.put("数字信封", digitalEnvelopeScore);
        jsonObject.put("页面停留时间", studyTime);
        jsonObject.put("MD5任务耗时", md5TaskTime);
        jsonObject.put("代码考核测试耗时", codeTestTime);

        return jsonObject.toJSONString();
    }

    @GetMapping("students")
    public List<Student> listStudentsPage(@RequestParam("page") int page) {
        return studentService.page(new Page<>(page * 10L, 10)).getRecords();
    }

}
