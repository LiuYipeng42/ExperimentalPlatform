package com.guet.ExperimentalPlatform.Initialize;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.mapper.StudentMapper;
import com.guet.ExperimentalPlatform.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class InitializeRedis implements ApplicationRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    private final StudentMapper studentMapper;

    private final UserService userService;

    public InitializeRedis(RedisTemplate<String, Object> redisTemplate, StudentMapper studentMapper,
                           UserService userService) {
        this.redisTemplate = redisTemplate;
        this.studentMapper = studentMapper;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {

        List<Student> students = studentMapper.selectList(
                new QueryWrapper<>()
        );

        redisTemplate.opsForValue().setBit("reportUpdate", students.size(), false);

        for (Student s : students) {
            redisTemplate.opsForValue().set("report:" + s.getId(), userService.calculateScore(s));
        }

    }
}
