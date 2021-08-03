package com.guet.ExperimentalPlatform.Interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Enumeration;

public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("------------------------------------------------------");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Start Time: " + new Date());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView){

        String key;

        HttpSession session = request.getSession();

        Enumeration<String> attributes = session.getAttributeNames();

        while (attributes.hasMoreElements()){
            key = attributes.nextElement();
            System.out.println(key + ": " + session.getAttribute(key));
        }

        System.out.println("End Time: " + new Date());
        System.out.println("------------------------------------------------------");
    }

}
