package com.guet.ExperimentalPlatform.service.impls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.*;
import com.guet.ExperimentalPlatform.mapper.*;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import com.guet.ExperimentalPlatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
public class UserServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements UserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StudentMapper studentMapper;
    private final LoginRecordMapper loginRecordMapper;
    private final StudyRecordMapper studyRecordMapper;
    private final AlgorithmRecordMapper algorithmRecordMapper;
    private final MD5TaskRecordMapper md5TaskRecordMapper;
    private final PORunCodesRecordMapper poRunCodesRecordMapper;
    private final CodeTestRecordMapper codeTestRecordMapper;
    private final FTEncryptionInfoMapper ftEncryptionInfoMapper;

    @Autowired
    public UserServiceImpl(RedisTemplate<String, Object> redisTemplate,
                           StudentMapper studentMapper, LoginRecordMapper loginRecordMapper,
                           StudyRecordMapper studyRecordMapper, AlgorithmRecordMapper algorithmRecordMapper,
                           MD5TaskRecordMapper md5TaskRecordMapper, PORunCodesRecordMapper poRunCodesRecordMapper,
                           CodeTestRecordMapper codeTestRecordMapper, FTEncryptionInfoMapper ftEncryptionInfoMapper) {
        this.redisTemplate = redisTemplate;

        this.studentMapper = studentMapper;
        this.loginRecordMapper = loginRecordMapper;
        this.studyRecordMapper = studyRecordMapper;
        this.algorithmRecordMapper = algorithmRecordMapper;
        this.md5TaskRecordMapper = md5TaskRecordMapper;
        this.poRunCodesRecordMapper = poRunCodesRecordMapper;
        this.codeTestRecordMapper = codeTestRecordMapper;
        this.ftEncryptionInfoMapper = ftEncryptionInfoMapper;
    }

    public String login(LoginForm loginForm) {
        Student student = studentMapper.selectOne(
                new QueryWrapper<Student>().eq("account", loginForm.account)
        );

        if (student == null) {
            return "没有此用户";
        }

        if (student.getPassword().equals(loginForm.password)) {

            LoginRecord loginRecord = new LoginRecord().setLoginTime(new Date()).setStudentId(student.getId());

            loginRecordMapper.insert(loginRecord);

            JSONObject loginResult = new JSONObject();

            if (student.getName().equals("teacher")) {
                loginResult.put("identity", "teacher");
            } else {
                loginResult.put("identity", "student");
            }
            loginResult.put("userName", student.getName());
            loginResult.put("userId", student.getId());
            loginResult.put("loginRecordId", loginRecord.getId());
            loginResult.put("timeStamp", System.currentTimeMillis());

            return loginResult.toJSONString();
        } else {
            return "密码错误";
        }

    }

    @Override
    public void deleteStudents(String column, String condition) {
        studentMapper.delete(
                new QueryWrapper<Student>().eq(column, condition)
        );
    }

    public JSONObject[] getStudentsInfo(List<Student> students){
        JSONObject[] studentsJSON = new JSONObject[students.size()];
        int index = 0;

        String score;
        JSONObject scoreJSON;
        JSONObject studentJSON;
        for (Student student : students) {
            scoreJSON = JSON.parseObject(getReport(student));
            score = (String) scoreJSON.get("总分");

            studentJSON = new JSONObject();
            studentJSON.put("account", student.getAccount());
            studentJSON.put("name", student.getName());
            studentJSON.put("score", Double.parseDouble(score));
            studentsJSON[index] = studentJSON;
            index++;
        }

        return studentsJSON;
    }

    public String getReport(Student student){

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

    @Override
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

        List<StudyRecord> studyRecords = studyRecordMapper.selectList(
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
        algorithmScore = (algorithmRecordMapper.selectList(
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
        List<MD5TaskRecord> md5TaskRecords = md5TaskRecordMapper.selectList(
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
        if (poRunCodesRecordMapper.selectList(
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
        if (poRunCodesRecordMapper.selectList(
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
        List<CodeTestRecord> codeTestRecords = codeTestRecordMapper.selectList(
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

        for (FTAllInfo info : ftEncryptionInfoMapper.getAllInfo(userId)) {

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
        jsonObject.put("总分", String.format("%.2f", algorithmScore * 0.2 + algorithmAttackScore * 0.3 + digitalEnvelopeScore * 0.5));
        jsonObject.put("页面停留时间", studyTime);
        jsonObject.put("MD5任务耗时", md5TaskTime);
        jsonObject.put("代码考核测试耗时", codeTestTime);
        jsonObject.put("总结", summary);

        return jsonObject.toJSONString();
    }

}
