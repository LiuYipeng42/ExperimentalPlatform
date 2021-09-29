package com.guet.ExperimentalPlatform.Service.impls;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.*;
import com.guet.ExperimentalPlatform.mapper.*;
import com.guet.ExperimentalPlatform.pojo.ClassPage;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import com.guet.ExperimentalPlatform.Service.UserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserMapper userMapper;
    private final LoginRecordMapper loginRecordMapper;
    private final StudyRecordMapper studyRecordMapper;
    private final AlgorithmRecordMapper algorithmRecordMapper;
    private final MD5TaskRecordMapper md5TaskRecordMapper;
    private final PORunCodesRecordMapper poRunCodesRecordMapper;
    private final CodeTestRecordMapper codeTestRecordMapper;
    private final FTEncryptionInfoMapper ftEncryptionInfoMapper;

    @Autowired
    public UserServiceImpl(RedisTemplate<String, Object> redisTemplate,
                           UserMapper userMapper, LoginRecordMapper loginRecordMapper,
                           StudyRecordMapper studyRecordMapper, AlgorithmRecordMapper algorithmRecordMapper,
                           MD5TaskRecordMapper md5TaskRecordMapper, PORunCodesRecordMapper poRunCodesRecordMapper,
                           CodeTestRecordMapper codeTestRecordMapper, FTEncryptionInfoMapper ftEncryptionInfoMapper) {
        this.redisTemplate = redisTemplate;

        this.userMapper = userMapper;
        this.loginRecordMapper = loginRecordMapper;
        this.studyRecordMapper = studyRecordMapper;
        this.algorithmRecordMapper = algorithmRecordMapper;
        this.md5TaskRecordMapper = md5TaskRecordMapper;
        this.poRunCodesRecordMapper = poRunCodesRecordMapper;
        this.codeTestRecordMapper = codeTestRecordMapper;
        this.ftEncryptionInfoMapper = ftEncryptionInfoMapper;
    }

    public String login(LoginForm loginForm) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>().select("password", "name", "id").eq("account", loginForm.account)
        );

        if (user == null) {
            return "没有此用户";
        }

        if (user.getPassword().equals(loginForm.password)) {

            LoginRecord loginRecord = new LoginRecord().setLoginTime(new Date()).setStudentId(user.getId());

            loginRecordMapper.insert(loginRecord);

            JSONObject loginResult = new JSONObject();

            if (user.getName().equals("teacher")) {
                loginResult.put("identity", "teacher");
            } else {
                loginResult.put("identity", "student");
            }
            loginResult.put("userName", user.getName());
            loginResult.put("userId", user.getId());
            loginResult.put("loginRecordId", loginRecord.getId());
            loginResult.put("timeStamp", System.currentTimeMillis());

            return loginResult.toJSONString();
        } else {
            return "密码错误";
        }

    }

    @Override
    public void deleteStudents(String column, String condition) {
        userMapper.delete(
                new QueryWrapper<User>().eq(column, condition)
        );
    }

    @Override
    public ClassPage<User> selectClassPage(ClassPage<User> classPage) {
        return userMapper.selectClassPage(classPage);
    }

    @Override
    public List<User> selectUserByClassNum(String classNum) {
        return userMapper.selectUserByClassNum(classNum);
    }

    @Override
    public Integer countStudentsByClassNum(String classNum) {
        return userMapper.countStudentsByClassNum(classNum);
    }

    @SuppressWarnings("unchecked")
    public void generateStudentScoreFile(String[] classes) throws IOException {

        String[] tittles = {"学号", "姓名", "代码测试耗时", "数字信封页面停留时间", "MD5碰撞耗时", "PaddingOracle页面停留时间", "认知理解能力", "操作实践能力", "攻防拓展能力", "总分"};
        Workbook workbook = new HSSFWorkbook();

        for (String classId: classes) {
            List<User> students = userMapper.selectUserByClassNum(classId);

            // 2.根据 workbook 创建 sheet
            Sheet sheet = workbook.createSheet(classId);
            sheet.setColumnWidth(0, 13 * 256);
            sheet.setColumnWidth(2, 14 * 256);
            sheet.setColumnWidth(3, 20 * 256);
            sheet.setColumnWidth(4, 13 * 256);
            sheet.setColumnWidth(5, 24 * 256);
            sheet.setColumnWidth(6, 13 * 256);
            sheet.setColumnWidth(7, 13 * 256);
            sheet.setColumnWidth(8, 13 * 256);

            // 3.根据 sheet 创建 row
            Row tittle = sheet.createRow(0);

            for (int i = 0; i < tittles.length; i++) {
                tittle.createCell(i).setCellValue(tittles[i]);
            }

            User student;
            Map<String, Object> report;
            double codeTestTime;
            double md5CollisionTime;
            HashMap<String, Double> stayTime;

            for (int r = 0; r < students.size(); r++) {
                student = students.get(r);
                report = getReport(student);

                codeTestTime = 0;
                for (Double time: ((HashMap<String, Double>) report.get("代码考核测试耗时")).values()) {
                    codeTestTime += time;
                }

                md5CollisionTime = 0;
                for (Double time: ((HashMap<String, Double>) report.get("MD5任务耗时")).values()) {
                    md5CollisionTime += time;
                }

                stayTime = (HashMap<String, Double>) report.get("页面停留时间");

                Row row = sheet.createRow(r + 1);
                row.createCell(0).setCellValue(student.getAccount());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(Double.parseDouble(String.format("%.2f", codeTestTime)));
                row.createCell(3).setCellValue(stayTime.get("1"));
                row.createCell(4).setCellValue(md5CollisionTime);
                row.createCell(5).setCellValue(stayTime.get("2"));
                row.createCell(6).setCellValue(Double.parseDouble((String) report.get("算法基础")));
                row.createCell(7).setCellValue(Double.parseDouble((String) report.get("数字信封")));
                row.createCell(8).setCellValue(Double.parseDouble((String) report.get("算法攻击")));
                row.createCell(9).setCellValue(Double.parseDouble((String) report.get("总分")));

            }
        }

        FileOutputStream fos = new FileOutputStream("StudentScoreFiles/学生成绩.xls");
        workbook.write(fos);
        fos.close();
    }

    public JSONObject[] getStudentsScore(List<User> users) {
        JSONObject[] studentsJSON = new JSONObject[users.size()];
        int index = 0;

        String score;
        Map<String, Object> scoreMap;
        JSONObject studentJSON;
        for (User user : users) {
            scoreMap = getReport(user);
            score = (String) scoreMap.get("总分");

            studentJSON = new JSONObject();
            studentJSON.put("account", user.getAccount());
            studentJSON.put("name", user.getName());
            studentJSON.put("score", Double.parseDouble(score));
            studentsJSON[index] = studentJSON;
            index++;
        }

        return studentsJSON;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getReport(User user) {

        long userId = user.getId();
        Map<String, Object> newReport;
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit("reportUpdate", userId))) {
            newReport = calculateScore(user);
            redisTemplate.opsForValue().set("report:" + userId, newReport);
            redisTemplate.opsForValue().setBit("reportUpdate", userId, false);
            return newReport;
        } else {

            Map<String, Object> report = (Map<String, Object>) redisTemplate.opsForValue().get("report:" + userId);

            if(report == null){
                newReport = calculateScore(user);
                redisTemplate.opsForValue().set("report:" + userId, newReport);
                return newReport;
            }

            return report;
        }

    }

    @Override
    public Map<String, Object> calculateScore(User user) {
        long userId = user.getId();
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

        String summary = user.getSummary();
        if (summary == null) {
            summary = "";
        }

        String comment = user.getComment();
        if (comment == null) {
            comment = "";
        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("算法基础", String.format("%.2f", algorithmScore));
        jsonObject.put("算法攻击", String.format("%.2f", algorithmAttackScore));
        jsonObject.put("数字信封", String.format("%.2f", digitalEnvelopeScore));
        jsonObject.put("总分", String.format("%.2f", algorithmScore * 0.2 + algorithmAttackScore * 0.3 + digitalEnvelopeScore * 0.5));
        jsonObject.put("页面停留时间", studyTime);
        jsonObject.put("MD5任务耗时", md5TaskTime);
        jsonObject.put("代码考核测试耗时", codeTestTime);
        jsonObject.put("总结", summary);
        jsonObject.put("评价", comment);

        return jsonObject;
    }

}
