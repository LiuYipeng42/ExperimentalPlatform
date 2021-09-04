package com.guet.ExperimentalPlatform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Utils.AES;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import com.guet.ExperimentalPlatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@CrossOrigin
@RestController
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestBody LoginForm loginForm) throws Exception {

        String secretKey = loginForm.account + loginForm.account.substring(4);

        loginForm.password = AES.Decrypt(loginForm.password, secretKey);

        String loginResult = userService.login(loginForm);

        if (loginResult.equals("没有此用户")){
            return "没有此用户";
        }else if (loginResult.equals("密码错误")){
            return "密码错误";
        } else  {

            JSONObject result = JSON.parseObject(loginResult);

            HttpSession session = request.getSession();
            session.setAttribute("userId", Long.valueOf(result.getString("userId")));
            session.setAttribute("loginId", Long.valueOf(result.getString("loginRecordId")));

            result.remove("loginRecordId");
            result.remove("userId");

            System.out.println(result);

            return AES.Encrypt(result.toString(), secretKey);
        }

    }

    @GetMapping("/changePasswd")
    public void changePassword(HttpServletRequest request, @RequestParam("passwd") String newPassword){

        long userId = (long) request.getSession().getAttribute("userId");

        userService.update(
                new UpdateWrapper<Student>().set("password", newPassword).eq("id", userId)
        );
    }

}
