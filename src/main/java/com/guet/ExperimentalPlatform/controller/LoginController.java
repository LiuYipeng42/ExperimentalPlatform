package com.guet.ExperimentalPlatform.controller;

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
    public String login(HttpServletRequest request, @RequestBody LoginForm loginForm) {
        String loginResult = studentService.login(loginForm);

        if (loginResult.contains("success")) {
            HttpSession httpSession = request.getSession();
            String userId = String.valueOf(loginResult.split(" ")[1]);
            httpSession.setAttribute(loginForm.account, userId);
            loginResult = "success";
        }
        return loginResult;
    }

}
