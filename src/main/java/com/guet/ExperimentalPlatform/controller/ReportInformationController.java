package com.guet.ExperimentalPlatform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.entity.*;
import com.guet.ExperimentalPlatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
public class ReportInformationController {

    private final UserService userService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ReportInformationController(UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
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

        return userService.getReport(student);

    }

    @GetMapping("/statistic")
    public String statisticScore(@RequestParam("classId") String classId) {
        List<Student> students;
        if (classId.equals("all")) {
            students = userService.list();
        } else {
            students = userService.list(
                    new QueryWrapper<Student>().eq("class_id", classId)
            );
        }

        int[] count = {0, 0, 0, 0, 0};

        double score;
        JSONObject scoreJson;
        for (Student student : students) {
            scoreJson = JSON.parseObject(getReport(student.getAccount()));
            score = Double.parseDouble((String) scoreJson.get("总分"));
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

}
