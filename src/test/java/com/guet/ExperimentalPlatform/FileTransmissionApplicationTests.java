package com.guet.ExperimentalPlatform;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.Entity.Class;
import com.guet.ExperimentalPlatform.Service.ClassService;
import com.guet.ExperimentalPlatform.Service.UserService;
import com.guet.ExperimentalPlatform.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTransmissionApplicationTests {

    @Autowired
    ClassService classService;

    @Autowired
    UserService userService;

    @Test
    public void test() {
//        System.out.println(userService.selectTeacherIdByAccount("1700301006"));
//        System.out.println(userService.selectTeacherIdByClassNum("1920746"));
    }

}
