package com.guet.ExperimentalPlatform.Utils;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

public class RedisOps {

    public static Number bitCount(RedisTemplate<String, Object> redisTemplate, String key) {

        Long num = redisTemplate.execute(
                (RedisCallback<Long>)
                        (redisConnection) -> redisConnection.bitCount(key.getBytes())
        );

        return Objects.requireNonNullElse(num, 0);

    }

}
