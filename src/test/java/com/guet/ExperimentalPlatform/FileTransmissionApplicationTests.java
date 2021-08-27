package com.guet.ExperimentalPlatform;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.entity.*;
import com.guet.ExperimentalPlatform.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTransmissionApplicationTests {

    @Autowired
    private AlgorithmRecordService algorithmRecordService;

    @Autowired
    private StudyRecordService studyRecordService;

    @Autowired
    private MD5CollisionService md5CollisionService;

    @Autowired
    private PORunCodesRecordService poRunCodesRecordService;

    @Autowired
    private CodeTestRecordService codeTestRecordService;

    @Autowired
    private FileTransmissionService fileTransmissionService;

    @Test
    public void test() {

        long userId = 10;

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
                    studyTime.get(s.getExperimentType()) + (s.getEndTime().getTime() - s.getStartTime().getTime()) / 1000
            );
        }

        // --------------------------------------------------------------------------------------
        // 运行分数 65 分
        algorithmScore = (algorithmRecordService.list(
                new QueryWrapper<AlgorithmRecord>().eq("student_id", userId)
        ).size() / 12.0) * 65;

        // rsa 10分钟 10分
        if (studyTime.get(4) < 600) {
            algorithmScore += (studyTime.get(4) / 600) * 10;
        } else {
            algorithmScore += 10;
        }

        // hash 10分钟 10分
        if (studyTime.get(5) < 600) {
            algorithmScore += (studyTime.get(5) / 1500) * 10;
        } else {
            algorithmScore += 10;
        }

        // aes 10分钟 15分
        if (studyTime.get(6) < 900) {
            algorithmScore += (studyTime.get(6) / 1500) * 15;
        } else {
            algorithmScore += 15;
        }

        System.out.println(algorithmScore);

        // --------------------------------------------------------------------------------------
        double md5CollisionScore;
        List<MD5TaskRecord> md5TaskRecords = md5CollisionService.list(
                new QueryWrapper<MD5TaskRecord>().eq("student_id", userId).eq("status", "finished")
        );

        // 四个任务一个 20 分，共 80 分
        md5CollisionScore = (md5TaskRecords.size() / 4.0) * 80;

        // 20 分钟 20分
        if (studyTime.get(3) < 1200) {
            md5CollisionScore += (studyTime.get(3) / 1200) * 20;
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
            md5CollisionScore += 20;
        }

        // 10 分钟 20 分
        if (studyTime.get(2) < 600) {
            paddingOracleScore += (studyTime.get(3) / 600) * 20;
        } else {
            paddingOracleScore += 20;
        }

        algorithmAttackScore = 0.5 * md5CollisionScore + 0.5 * paddingOracleScore;

        System.out.println(algorithmAttackScore);

        // --------------------------------------------------------------------------------------

        // 代码考核 50分
        List<CodeTestRecord> codeTestRecords = codeTestRecordService.list(
                new QueryWrapper<CodeTestRecord>().eq("student_id", userId)
        );
        digitalEnvelopeScore = (codeTestRecords.size() / 6.0) * 50;

        boolean sender = false;
        boolean receiver = false;

        for (FTAllInfo info : fileTransmissionService.getAllInfo(userId)) {

            if (info.getSenderId() == userId){
                sender = true;
            }

            if (info.getReceiverId() == userId){
                receiver = true;
            }
        }

        // 两次传输，一次发送方 25分，一次接收方 25分
        if (sender){
            digitalEnvelopeScore += 25;
        }

        if (receiver){
            digitalEnvelopeScore += 25;
        }

        System.out.println(digitalEnvelopeScore);

        studyTime.forEach((k, v)-> System.out.println());

    }

}
