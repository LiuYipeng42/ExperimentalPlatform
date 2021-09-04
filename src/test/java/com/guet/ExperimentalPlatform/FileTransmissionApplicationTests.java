package com.guet.ExperimentalPlatform;

import com.guet.ExperimentalPlatform.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTransmissionApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {

//        redisTemplate.opsForValue().setBit("reportUpdate", 105, false);
//
//        for (int i = 0; i < 106; i++) {
//            System.out.println(redisTemplate.opsForValue().getBit("reportUpdate", i));
//        }
//        redisTemplate.delete("reportUpdate");
    }

}
