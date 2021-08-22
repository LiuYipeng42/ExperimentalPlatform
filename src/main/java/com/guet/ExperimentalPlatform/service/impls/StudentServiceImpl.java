package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.LoginRecord;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.mapper.LoginRecordMapper;
import com.guet.ExperimentalPlatform.mapper.StudentMapper;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import com.guet.ExperimentalPlatform.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements StudentService {

    private final StudentMapper studentMapper;
    private final LoginRecordMapper loginRecordMapper;

    @Autowired
    public StudentServiceImpl(StudentMapper studentMapper, LoginRecordMapper loginRecordMapper) {
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

            return "success " + student.getId() + " " + loginRecord.getId();
        } else {
            return "密码错误";
        }

    }

}
