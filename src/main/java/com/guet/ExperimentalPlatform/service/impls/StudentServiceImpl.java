package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.mapper.StudentMapper;
import com.guet.ExperimentalPlatform.mapper.StudyRecordMapper;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import com.guet.ExperimentalPlatform.service.StudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements StudentService {

    private StudentMapper studentMapper;
    private StudyRecordMapper studyRecordMapper;

    @Resource
    public void setStudentMapper(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Resource
    public void setStudyRecordMapper(StudyRecordMapper studyRecordMapper) {
        this.studyRecordMapper = studyRecordMapper;
    }

    public String login(LoginForm loginForm) {
        Student student = studentMapper.selectOne(
                new QueryWrapper<Student>().eq("account", loginForm.account)
        );

        if (student == null) {
            return "没有此用户";
        }

        StudyRecord studyRecord = studyRecordMapper.selectOne(
                new QueryWrapper<StudyRecord>().eq("student_id", student.getId()).isNull("logout_time")
        );

        if (studyRecord != null) {
            return "此账号已登录";
        }

        if (student.getPassword().equals(loginForm.password)) {
            return "success " + student.getId();
        } else {
            return "密码错误";
        }

    }

}
