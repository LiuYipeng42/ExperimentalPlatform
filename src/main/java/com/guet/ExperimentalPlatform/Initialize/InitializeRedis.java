package com.guet.ExperimentalPlatform.Initialize;

import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.mapper.UserMapper;
import com.guet.ExperimentalPlatform.Service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


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

        RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
        connection.flushDb();

        List<User> users = userMapper.getAllStudents();

        redisTemplate.opsForValue().setBit("reportUpdate", users.size(), false);

        for (User s : users) {
            redisTemplate.opsForValue().set("report:" + s.getId(), userService.calculateScore(s));
        }

    }
}
