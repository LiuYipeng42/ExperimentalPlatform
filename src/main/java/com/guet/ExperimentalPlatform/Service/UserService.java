package com.guet.ExperimentalPlatform.Service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.pojo.LoginForm;

import java.util.List;


public interface UserService extends IService<User> {
    String login(LoginForm loginForm);

    String calculateScore(User user);

    String getReport(User user);

    JSONObject[] getStudentsInfo(List<User> users);

    void deleteStudents(String column, String condition);
}
