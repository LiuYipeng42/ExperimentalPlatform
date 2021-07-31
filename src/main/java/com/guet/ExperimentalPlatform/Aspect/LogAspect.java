package com.guet.ExperimentalPlatform.Aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAspect {

    @Pointcut("within(com.guet.ExperimentalPlatform.controller.PaddingOracleController)")
    private void PaddingOracleController(){}
    @Pointcut("within(com.guet.ExperimentalPlatform.controller.MD5CollisionController)")
    private void MD5CollisionController(){}

    @Before(value = "(PaddingOracleController() || MD5CollisionController()) && args(request, userAccount)",
            argNames = "request,userAccount")
    public void log(HttpServletRequest request, String userAccount) {
        getInfo(request, userAccount);
    }

    @Before(value = "(PaddingOracleController() || MD5CollisionController()) && args(request, userAccount, fileName)",
            argNames = "request,userAccount,fileName")
    public void log(HttpServletRequest request, String userAccount, String fileName) {
        getInfo(request, userAccount);
        System.out.println(fileName);
    }

    private void getInfo(HttpServletRequest request, String userAccount){
        System.out.println("------------------------------------------------------");
        String className;
        for (StackTraceElement c: Thread.currentThread().getStackTrace()) {
            className = c.toString();
            if (className.startsWith("com.guet.ExperimentalPlatform.controller")){
                System.out.println(className.substring(97));
            }
        }
        System.out.println("Session: " + request.getSession());
        System.out.println("Account: " + userAccount + " Id: " + request.getSession().getAttribute(userAccount));
    }

}
