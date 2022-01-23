package com.guet.ExperimentalPlatform;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.Entity.Class;
import com.guet.ExperimentalPlatform.Entity.PORunCodesRecord;
import com.guet.ExperimentalPlatform.Service.ClassService;
import com.guet.ExperimentalPlatform.Service.UserService;
import com.guet.ExperimentalPlatform.mapper.PORunCodesRecordMapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ExperimentalPlatformApplicationTests {

    @Autowired
    PORunCodesRecordMapper poRunCodesRecordMapper;

    @Test
    public void test() {
        poRunCodesRecordMapper.selectList(
                new QueryWrapper<PORunCodesRecord>()
                        .select("DISTINCT code_type")
                        .eq("student_id", 5)
                        .ne("code_type", "auto_attack")
                        .eq("status", "success")
        ).forEach(System.out::println);


    }

}
