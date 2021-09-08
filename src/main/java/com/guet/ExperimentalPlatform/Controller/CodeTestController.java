package com.guet.ExperimentalPlatform.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Entity.CodeTestRecord;
import com.guet.ExperimentalPlatform.Service.CodeTestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@CrossOrigin
@RestController
@RequestMapping("CodeTest")
public class CodeTestController {

    private final CodeTestRecordService codeTestRecordService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CodeTestController(CodeTestRecordService codeTestRecordService, RedisTemplate<String, Object> redisTemplate) {
        this.codeTestRecordService = codeTestRecordService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/start")
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

    @GetMapping("/finish")
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

    @GetMapping("/finished")
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
