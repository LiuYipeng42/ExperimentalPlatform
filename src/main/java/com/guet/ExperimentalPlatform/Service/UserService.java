package com.guet.ExperimentalPlatform.Service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.pojo.ClassPage;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface UserService extends IService<User> {
    String login(LoginForm loginForm);

    Map<String, Object> calculateScore(User user);

    Map<String, Object> getReport(User user);

    JSONObject[] getStudentsScore(List<User> users);

    void generateStudentScoreFile(String[] classes, String path) throws IOException;

    void deleteStudents(long studentId);

    List<User> selectUserByClassNum(String classNum);

    List<User> selectUserByTeacher(String teacherId);

    Integer countStudentsByClassNum(String classNum);

    List<Long> selectTeacherIdByAccount(String account);

    List<Long> selectTeacherIdById(long id);

}
