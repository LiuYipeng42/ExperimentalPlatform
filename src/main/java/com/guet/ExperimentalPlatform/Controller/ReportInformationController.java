package com.guet.ExperimentalPlatform.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Entity.*;
import com.guet.ExperimentalPlatform.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
        redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

        userService.update(
                new UpdateWrapper<User>().set("summary", FileOperation.getPostData(request)).eq("id", userId)
        );

    }

    @PostMapping("/saveComment")
    public void saveComment(HttpServletRequest request, @RequestParam("userAccount") String userAccount) {

        long userId = userService.getOne(
                new QueryWrapper<User>().eq("account", userAccount)
        ).getId();

        redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

        userService.update(
                new UpdateWrapper<User>().set("comment", FileOperation.getPostData(request)).eq("id", userId)
        );

    }

    @GetMapping("report")
    public String getReport(@RequestParam("userAccount") String userAccount) {

        User user = userService.getOne(new QueryWrapper<User>().eq("account", userAccount));

        return userService.getReport(user).toString();

    }

    @GetMapping("/statistic")
    public String statisticScore(@RequestParam("classId") String classId) {
        List<User> users;
        if (classId.equals("all")) {
            users = userService.list();
        } else {
            users = userService.selectUserByClassNum(classId);
        }

        int[] count = {0, 0, 0, 0, 0};

        double score;
        JSONObject scoreJson;
        for (User user : users) {
            scoreJson = JSON.parseObject(getReport(user.getAccount()));
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

    @PostMapping("/downLoadScores")
    public ResponseEntity<ByteArrayResource> downLoadReports(@RequestBody String[] classes) throws IOException {

        userService.generateStudentScoreFile(classes);

        File file = new File("StudentScoreFiles/学生成绩.xls");

        HttpHeaders header = new HttpHeaders();

        // Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
        // 当 浏览器 接收到头时，它会激活文件下载对话框，它的文件名框自动填充了头中指定的文件名。
        // （请注意，这是设计导致的；无法使用此功能将文档保存到用户的计算机上，而不向用户询问保存位置。）
        // 服务端向客户端游览器发送文件时，如果是浏览器支持的文件类型，一般会默认使用浏览器打开，比如txt、jpg等，
        // 如果需要提示用户保存，就要利用 Content-Disposition 进行一下处理，关键在于一定要加上attachment：
        header.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + URLEncoder.encode("学生成绩.xls", StandardCharsets.UTF_8)
        );

        // 清除缓存
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }
}
