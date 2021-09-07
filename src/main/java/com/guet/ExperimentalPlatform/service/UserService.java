package com.guet.ExperimentalPlatform.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import java.util.List;


public interface UserService extends IService<Student> {
    String login(LoginForm loginForm);

    String calculateScore(Student student);

    String getReport(Student student);

    JSONObject[] getStudentsInfo(List<Student> students);

    void deleteStudents(String column, String condition);
}
