package com.guet.ExperimentalPlatform.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.pojo.LoginForm;


public interface StudentService extends IService<Student> {
    String login(LoginForm loginForm);
}
