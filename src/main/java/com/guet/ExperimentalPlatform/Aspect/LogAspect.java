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

    @Before(value = "(PaddingOracleController() || MD5CollisionController()) && args(request, userAccount, otherData)",
            argNames = "request,userAccount,otherData")
    public void log(HttpServletRequest request, String userAccount, String otherData) {
        getInfo(request, userAccount);
        System.out.println(otherData);
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
        System.out.println("Account: " + userAccount + " Id: " + request.getSession().getAttribute(userAccount));
    }

}
