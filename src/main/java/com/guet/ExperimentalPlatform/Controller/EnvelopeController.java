package com.guet.ExperimentalPlatform.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Entity.CodeTestRecord;
import com.guet.ExperimentalPlatform.Entity.CognitionTestRecord;
import com.guet.ExperimentalPlatform.Entity.DragTestRecord;
import com.guet.ExperimentalPlatform.Service.CodeTestRecordService;
import com.guet.ExperimentalPlatform.Service.CognitionTestRecordService;
import com.guet.ExperimentalPlatform.Service.DragTestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("envelope")
public class EnvelopeController {

    private final CodeTestRecordService codeTestRecordService;

    private final DragTestRecordService dragTestRecordService;

    private final CognitionTestRecordService cognitionTestRecordService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public EnvelopeController(CodeTestRecordService codeTestRecordService, DragTestRecordService dragTestRecordService, CognitionTestRecordService cognitionTestRecordService, RedisTemplate<String, Object> redisTemplate) {
        this.codeTestRecordService = codeTestRecordService;
        this.dragTestRecordService = dragTestRecordService;
        this.cognitionTestRecordService = cognitionTestRecordService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/dragTest/finish")
    public void dragSuccess(HttpServletRequest request, @RequestParam("step") int step){

        if (step < 7 && step > 0) {

            long userId = (long) request.getSession().getAttribute("userId");

            dragTestRecordService.save(
                    new DragTestRecord().setStudentId(userId).setStep(step)
            );

            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

        }

    }

    @GetMapping("/dragTest/finished")
    public int[] finishedSteps(HttpServletRequest request){

        long userId = (long) request.getSession().getAttribute("userId");

        List<DragTestRecord> finishedTests = dragTestRecordService.list(
                new QueryWrapper<DragTestRecord>().eq("student_id", userId)
        );

        int[] bitMap = new int[]{0, 0, 0, 0, 0, 0};

        for (DragTestRecord record : finishedTests) {
            bitMap[record.getStep() - 1] = 1;
        }

        return bitMap;

    }

    @GetMapping("/cognitionTest/finish")
    public void finishCognition(HttpServletRequest request, @RequestParam("step") int step) {

        if (step < 7 && step > 0) {

            System.out.println(step);

            long userId = (long) request.getSession().getAttribute("userId");

            cognitionTestRecordService.save(
                    new CognitionTestRecord().setStudentId(userId).setStep(step)
            );

            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

        }
    }

    @GetMapping("/cognitionTest/finished")
    public int[] finishedCognition(HttpServletRequest request) {

        long userId = (long) request.getSession().getAttribute("userId");

        List<CognitionTestRecord> finishedTests = cognitionTestRecordService.list(
                new QueryWrapper<CognitionTestRecord>().eq("student_id", userId)
        );

        int[] bitMap = new int[]{0, 0, 0, 0, 0, 0};

        for (CognitionTestRecord record : finishedTests) {
            bitMap[record.getStep() - 1] = 1;
        }

        return bitMap;
    }

    @GetMapping("/codeTest/start")
    public void startTest(HttpServletRequest request, @RequestParam("type") String codesType) {

        long userId = (long) request.getSession().getAttribute("userId");

        codeTestRecordService.remove(
                new QueryWrapper<CodeTestRecord>()
                        .eq("student_id", userId)
                        .eq("code_type", codesType)
                        .isNull("end_time")
        );

        if (codeTestRecordService.getOne(
                new QueryWrapper<CodeTestRecord>().eq("student_id", userId).eq("code_type", codesType)
        ) == null) {

            codeTestRecordService.save(
                    new CodeTestRecord().setStudentId(userId).setCodeType(codesType).setStartTime(new Date())
            );
        }

    }

    @GetMapping("/codeTest/finish")
    public void finishTest(HttpServletRequest request, @RequestParam("type") String codesType) {
        long userId = (long) request.getSession().getAttribute("userId");

        codeTestRecordService.update(
                new UpdateWrapper<CodeTestRecord>()
                        .set("end_time", new Date())
                        .eq("student_id", userId)
                        .eq("code_type", codesType)
                        .isNull("end_time")
        );
        redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

    }

    @GetMapping("/codeTest/finished")
    public int[] finishedTests(HttpServletRequest request) {

        long userId = (long) request.getSession().getAttribute("userId");

        Integer[] finishedTests = codeTestRecordService.
                list(new QueryWrapper<CodeTestRecord>().eq("student_id", userId).isNotNull("end_time"))
                .stream().map(x->Integer.valueOf(x.getCodeType())).toArray(Integer[]::new);

        int[] bitMap = new int[]{0, 0, 0, 0, 0, 0};

        for (int test : finishedTests) {
            bitMap[test - 1] = 1;
        }

        return bitMap;
    }

}
