package com.guet.ExperimentalPlatform.Listener;


import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
 import java.util.Enumeration;

@WebListener
public class SessionListener implements HttpSessionListener {

    PaddingOracleService paddingOracleService;

    @Autowired
    public SessionListener(PaddingOracleService paddingOracleService){
        this.paddingOracleService = paddingOracleService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        System.out.println("Session: " + arg0.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {

        String userId = getAttributes(arg0).split(": ")[1].strip();

        paddingOracleService.closeEnvironment(userId);

        System.out.println("User " + userId + " logout!");
    }

    private String getAttributes(HttpSessionEvent arg0) {

        StringBuilder sessionData = new StringBuilder();

        HttpSession session = arg0.getSession();

        Enumeration<String> e = session.getAttributeNames();
        String key;
        while (e.hasMoreElements()) {
            key = e.nextElement();
            sessionData.append(key).append(": ").append(session.getAttribute(key)).append("\n");
        }

        return sessionData.toString();
    }
}
