package com.guet.ExperimentalPlatform.Initialize;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.mapper.UserMapper;
import com.guet.ExperimentalPlatform.Service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class InitializeRedis implements ApplicationRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserMapper userMapper;

    private final UserService userService;

    public InitializeRedis(RedisTemplate<String, Object> redisTemplate, UserMapper userMapper,
                           UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {

        List<User> users = userMapper.selectList(
                new QueryWrapper<>()
        );

        redisTemplate.opsForValue().setBit("reportUpdate", users.size(), false);

        for (User s : users) {
            redisTemplate.opsForValue().set("report:" + s.getId(), userService.calculateScore(s));
        }

    }
}
