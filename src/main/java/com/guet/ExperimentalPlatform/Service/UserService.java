package com.guet.ExperimentalPlatform.Service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.pojo.ClassPage;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface UserService extends IService<User> {
    String login(LoginForm loginForm);

    Map<String, Object> calculateScore(User user);

    Map<String, Object> getReport(User user);

    JSONObject[] getStudentsScore(List<User> users);

    void generateStudentScoreFile(String[] classes) throws IOException;

    void deleteStudents(String column, String condition);

    ClassPage<User> selectClassPage(ClassPage<User> classPage);

    List<User> selectUserByClassNum(String classNum);

    Integer countStudentsByClassNum(String classNum);
}
