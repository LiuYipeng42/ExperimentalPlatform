package com.guet.ExperimentalPlatform.controller;

import com.guet.ExperimentalPlatform.Utils.AES;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import com.guet.ExperimentalPlatform.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@CrossOrigin
@RestController
public class LoginController {
    private final StudentService studentService;

    @Autowired
    public LoginController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestBody LoginForm loginForm) throws Exception {

        String secretKey = loginForm.account + loginForm.account.substring(4);

        loginForm.password = AES.Decrypt(loginForm.password, secretKey);

        String[] loginResult = studentService.login(loginForm).split(" ");
        String result;

        if (loginResult[0].equals("没有此用户")){
            return "没有此用户";
        }else {
            if (loginResult[0].equals("success")) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", Long.valueOf(loginResult[1]));
                session.setAttribute("loginId", Long.valueOf(loginResult[2]));
            }

            result = loginResult[0] + System.currentTimeMillis();

            return AES.Encrypt(result, secretKey);
        }

    }

}
