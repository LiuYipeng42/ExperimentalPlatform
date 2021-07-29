package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.mapper.StudentMapper;
import com.guet.ExperimentalPlatform.mapper.StudyRecordMapper;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import com.guet.ExperimentalPlatform.pojo.RequestResult;

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

    public RequestResult<Student> login(LoginForm loginForm) {
        Student student = studentMapper.selectOne(
                new QueryWrapper<Student>().eq("account", loginForm.account)
        );

        if (student == null) {
            return new RequestResult<>(200, "can't find this user!", null);
        }

        StudyRecord studyRecord = studyRecordMapper.selectOne(
                new QueryWrapper<StudyRecord>().eq("student_id", student.getId()).isNull("logout_time")
        );

        if (studyRecord != null) {
            return new RequestResult<>(200, "此账号已登录", null);
        }

        if (student.getPassword().equals(loginForm.password)) {
            return new RequestResult<>(200, "success", student);
        } else {
            return new RequestResult<>(200, "wrong password!", null);
        }

    }

}
