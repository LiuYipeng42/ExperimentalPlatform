package com.guet.ExperimentalPlatform.controller;

import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import com.guet.ExperimentalPlatform.pojo.RequestResult;
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
    public RequestResult<Student> login(HttpServletRequest request, @RequestBody LoginForm loginForm) {
        RequestResult<Student> requestResult = studentService.login(loginForm);

        HttpSession httpSession = request.getSession();
        String userId = String.valueOf(requestResult.getData().getId());

        httpSession.setAttribute(loginForm.account, userId);
        httpSession.setAttribute("userId", userId);
        return requestResult;
    }

    @GetMapping("/userId")
    public String getUserId(HttpServletRequest request){
        System.out.println((String) request.getSession().getAttribute("userId"));
        return (String) request.getSession().getAttribute("userId");
    }



}
