package com.guet.ExperimentalPlatform.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ExperimentalPlatform.Utils.AES;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import com.guet.ExperimentalPlatform.Service.UserService;
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

    @PostMapping("/checkTeacher")
    public String checkTeacher(@RequestBody LoginForm loginForm) throws Exception {

        String account = loginForm.account;
        String secretKey = account + account.substring(2 * account.length() - 16);

        loginForm.password = AES.Decrypt(loginForm.password, secretKey);

        String loginResult = userService.login(loginForm);

        if (loginResult.equals("密码错误")){
            return "密码错误";
        } else {
            return AES.Encrypt("success " + System.currentTimeMillis(), secretKey);
        }
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestBody LoginForm loginForm) throws Exception {

        String account = loginForm.account;
        String secretKey = account + account.substring(2 * account.length() - 16);

        System.out.println(1);
        loginForm.password = AES.Decrypt(loginForm.password, secretKey);

        System.out.println(2);
        String loginResult = userService.login(loginForm);
        System.out.println(3);

        if (loginResult.equals("没有此用户")){
            return "没有此用户";
        }else if (loginResult.equals("密码错误")){
            return "密码错误";
        } else  {

            JSONObject result = JSON.parseObject(loginResult);

            HttpSession session = request.getSession();
            System.out.println(4);
            session.setAttribute("userId", Long.valueOf(result.getString("userId")));
            session.setAttribute("loginId", Long.valueOf(result.getString("loginRecordId")));

            result.remove("loginRecordId");
            result.remove("userId");
            System.out.println(5);
            System.out.println(result);

            return AES.Encrypt(result.toString(), secretKey);
        }

    }

}
