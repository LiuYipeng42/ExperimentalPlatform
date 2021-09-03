package com.guet.ExperimentalPlatform.service.impls;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.LoginRecord;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.mapper.LoginRecordMapper;
import com.guet.ExperimentalPlatform.mapper.StudentMapper;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import com.guet.ExperimentalPlatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UserServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements UserService {

    private final StudentMapper studentMapper;
    private final LoginRecordMapper loginRecordMapper;

    @Autowired
    public UserServiceImpl(StudentMapper studentMapper, LoginRecordMapper loginRecordMapper) {
        this.studentMapper = studentMapper;
        this.loginRecordMapper = loginRecordMapper;
    }

    public String login(LoginForm loginForm) {
        Student student = studentMapper.selectOne(
                new QueryWrapper<Student>().eq("account", loginForm.account)
        );

        if (student == null) {
            return "没有此用户";
        }

        if (student.getPassword().equals(loginForm.password)) {

            LoginRecord loginRecord = new LoginRecord().setLoginTime(new Date()).setStudentId(student.getId());

            loginRecordMapper.insert(loginRecord);

            JSONObject loginResult = new JSONObject();

            if (student.getName().equals("teacher")) {
                loginResult.put("identity", "teacher");
            } else {
                loginResult.put("identity", "student");
            }
            loginResult.put("userName", student.getName());
            loginResult.put("userId", student.getId());
            loginResult.put("loginRecordId", loginRecord.getId());
            loginResult.put("timeStamp", System.currentTimeMillis());

            return loginResult.toJSONString();
        } else {
            return "密码错误";
        }

    }

}
