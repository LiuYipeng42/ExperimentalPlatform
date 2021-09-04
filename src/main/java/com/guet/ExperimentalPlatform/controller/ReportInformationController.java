package com.guet.ExperimentalPlatform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.entity.*;
import com.guet.ExperimentalPlatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class ReportInformationController {

    private final UserService userService;

    private final AlgorithmRecordService algorithmRecordService;

    private final StudyRecordService studyRecordService;

    private final MD5CollisionService md5CollisionService;

    private final CodeTestRecordService codeTestRecordService;

    private final FileTransmissionService fileTransmissionService;

    private final PaddingOracleService paddingOracleService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ReportInformationController(
            UserService userService,
            AlgorithmRecordService algorithmRecordService,
            StudyRecordService studyRecordService,
            MD5CollisionService md5CollisionService,
            CodeTestRecordService codeTestRecordService,
            FileTransmissionService fileTransmissionService,
            PaddingOracleService paddingOracleService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.algorithmRecordService = algorithmRecordService;
        this.studyRecordService = studyRecordService;
        this.md5CollisionService = md5CollisionService;
        this.codeTestRecordService = codeTestRecordService;
        this.fileTransmissionService = fileTransmissionService;
        this.paddingOracleService = paddingOracleService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/saveSummary")
    public void saveSummary(HttpServletRequest request) {
        long userId = (long) request.getSession().getAttribute("userId");

        userService.update(
                new UpdateWrapper<Student>().set("summary", FileOperation.getPostData(request)).eq("id", userId)
        );

    }


    @GetMapping("report")
    public String getReport(@RequestParam("userAccount") String userAccount) {

        Student student = userService.getOne(new QueryWrapper<Student>().eq("account", userAccount));

        long userId = student.getId();

        if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit("reportUpdate", userId))) {
            String newReport = calculateScore(student);
            redisTemplate.opsForValue().set("report:" + userId, newReport);
            redisTemplate.opsForValue().setBit("reportUpdate", userId, false);
            return newReport;
        } else {
            return (String) redisTemplate.opsForValue().get("report:" + userId);
        }

    }

    @GetMapping("/statistic")
    public String statisticScore() {

        List<Student> students = userService.list();

        int[] count = {0, 0, 0, 0, 0};

        double score;
        JSONObject scoreJson;
        for (Student student : students) {
            scoreJson = JSON.parseObject(getReport(student.getAccount()));
            score = Double.parseDouble(((BigDecimal) scoreJson.get("总分")).toPlainString());
            if (0 < score && score <= 20) {
                count[0]++;
            } else if (20 < score && score <= 40) {
                count[1]++;
            } else if (40 < score && score <= 60) {
                count[2]++;
            } else if (60 < score && score <= 80) {
                count[3]++;
            } else if (80 < score && score <= 100) {
                count[4]++;
            }
        }

        HashMap<String, Integer> scoreStatistic = new HashMap<>();
        scoreStatistic.put("0-20", count[0]);
        scoreStatistic.put("20-40", count[1]);
        scoreStatistic.put("40-60", count[2]);
        scoreStatistic.put("60-80", count[3]);
        scoreStatistic.put("80-100", count[4]);

        return scoreStatistic.toString();

    }


    @GetMapping("students")
    public String listStudentsPage(@RequestParam("page") int page) {

        int count = userService.count();
        int pageNum;

        if (count % 10 != 0) {
            pageNum = count / 10 + 1;
        } else {
            pageNum = count / 10;
        }

        List<Student> studentInfos = userService.page(new Page<>(page + 1, 10)).getRecords();

//        studentInfos.forEach((x)-> System.out.print(x.getId() + " "));


        JSONObject[] students = new JSONObject[10];
        int index = 0;

        BigDecimal score;
        JSONObject scoreJson;
        JSONObject student;
        for (Student studentInfo : studentInfos) {
            scoreJson = JSON.parseObject(getReport(studentInfo.getAccount()));
            score = (BigDecimal) scoreJson.get("总分");

            student = new JSONObject();
            student.put("account", studentInfo.getAccount());
            student.put("name", studentInfo.getName());
            student.put("score", Double.parseDouble(score.toString()));
            students[index] = student;
            index++;
        }

        JSONObject result = new JSONObject();
        result.put("pageNum", pageNum);
        result.put("students", students);

        return result.toJSONString();
    }


    public String calculateScore(Student student) {

        long userId = student.getId();
        double algorithmScore;
        double algorithmAttackScore;
        double digitalEnvelopeScore;

        HashMap<String, Double> studyTime = new HashMap<>();

        studyTime.put("1", 0.0);
        studyTime.put("2", 0.0);
        studyTime.put("3", 0.0);
        studyTime.put("4", 0.0);
        studyTime.put("5", 0.0);
        studyTime.put("6", 0.0);
        studyTime.put("7", 0.0);

        List<StudyRecord> studyRecords = studyRecordService.list(
                new QueryWrapper<StudyRecord>()
                        .eq("student_id", userId)
                        .isNotNull("end_time")
        );

        for (StudyRecord s : studyRecords) {

            if (s.getExperimentType() == 7 || s.getExperimentType() == 8) {
                studyTime.put(
                        "6",
                        studyTime.get("6") + (s.getEndTime().getTime() - s.getStartTime().getTime()) / 60000.0
                );
            } else if (s.getExperimentType() == 9) {
                studyTime.put(
                        "7",
                        studyTime.get("7") + (s.getEndTime().getTime() - s.getStartTime().getTime()) / 60000.0
                );
            } else {
                studyTime.put(
                        String.valueOf(s.getExperimentType()),
                        studyTime.get(String.valueOf(s.getExperimentType())) + (s.getEndTime().getTime() - s.getStartTime().getTime()) / 60000.0
                );
            }

        }

        // --------------------------------------------------------------------------------------
        // 运行分数 65 分
        algorithmScore = (algorithmRecordService.list(
                new QueryWrapper<AlgorithmRecord>().eq("student_id", userId)
        ).size() / 12.0) * 65;

        // rsa 10分钟 10分
        if (studyTime.get("4") < 10) {
            algorithmScore += studyTime.get("4");
        } else {
            algorithmScore += 10;
        }

        // hash 10分钟 10分
        if (studyTime.get("5") < 10) {
            algorithmScore += studyTime.get("5");
        } else {
            algorithmScore += 10;
        }

        // aes 10分钟 15分
        if (studyTime.get("6") < 10) {
            algorithmScore += studyTime.get("6") * 1.5;
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
        if (studyTime.get("3") < 20) {
            md5CollisionScore += studyTime.get("3");
        } else {
            md5CollisionScore += 20;
        }

        // --------------------------------------------------------------------------------------
        double paddingOracleScore;
        // 完成 auto attack 60 分
        if (paddingOracleService.list(
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
        if (paddingOracleService.list(
                new QueryWrapper<PORunCodesRecord>()
                        .eq("student_id", userId)
                        .eq("code_type", "manual_attack")
        ).size() > 0) {
            paddingOracleScore += 20;
        }

        // 10 分钟 20 分
        if (studyTime.get("2") < 10) {
            paddingOracleScore += studyTime.get("2") * 0.5;
        } else {
            paddingOracleScore += 20;
        }

        algorithmAttackScore = 0.5 * md5CollisionScore + 0.5 * paddingOracleScore;

        // --------------------------------------------------------------------------------------
        // --------------------------------------------------------------------------------------

        // 代码考核 50分
        List<CodeTestRecord> codeTestRecords = codeTestRecordService.list(
                new QueryWrapper<CodeTestRecord>().eq("student_id", userId).isNotNull("end_time")
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

//        md5TaskTime.forEach((k, v) -> System.out.println(k + " " + v));
//
//        codeTestTime.forEach((k, v) -> System.out.println(k + " " + v));
//
//        studyTime.forEach((k, v) -> System.out.println(k + " " + v));

        String summary = student.getSummary();
        if (summary == null) {
            summary = "";
        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("算法基础", algorithmScore);
        jsonObject.put("算法攻击", algorithmAttackScore);
        jsonObject.put("数字信封", digitalEnvelopeScore);
        jsonObject.put("总分", algorithmScore * 0.2 + algorithmAttackScore * 0.3 + digitalEnvelopeScore * 0.5);
        jsonObject.put("页面停留时间", studyTime);
        jsonObject.put("MD5任务耗时", md5TaskTime);
        jsonObject.put("代码考核测试耗时", codeTestTime);
        jsonObject.put("总结", summary);

        return jsonObject.toJSONString();
    }


}
