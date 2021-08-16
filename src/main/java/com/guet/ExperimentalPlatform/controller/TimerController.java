package com.guet.ExperimentalPlatform.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class TimerController {

    @GetMapping("/heartbeat?")
    public void heartbeat(HttpServletRequest request,
                          String page){
        HttpSession session = request.getSession();

        String presentPage = (String) session.getAttribute("page");

        if (!page.equals(presentPage)){
            session.setAttribute("page", page);
        }

    }
}
