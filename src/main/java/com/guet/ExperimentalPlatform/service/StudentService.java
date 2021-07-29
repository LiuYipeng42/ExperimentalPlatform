package com.guet.ExperimentalPlatform.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import com.guet.ExperimentalPlatform.pojo.RequestResult;


public interface StudentService extends IService<Student> {
    RequestResult<Student> login(LoginForm loginForm);
}
