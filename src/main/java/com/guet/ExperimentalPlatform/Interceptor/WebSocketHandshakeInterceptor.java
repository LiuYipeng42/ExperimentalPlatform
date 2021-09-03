package com.guet.ExperimentalPlatform.Interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Date;
import java.util.Map;


@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    UserService userService;

    @Autowired
    public void setStudentService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                   ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler,
                                   Map<String, Object> map) {

        try {
            String userAccount = ((ServletServerHttpRequest) serverHttpRequest)
                    .getServletRequest()
                    .getParameter("userAccount");

            System.out.println(userService);
            long userId = userService.getOne(
                    new QueryWrapper<Student>().eq("account", userAccount)
            ).getId();

            map.put("userId", userId);
            map.put("userAccount", userAccount);

        } catch (NullPointerException e){
            System.out.println("WebSocketHandshakeInterceptor:NullPointerException " + new Date());
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler,
                               Exception e) {
        assert e != null;
        e.printStackTrace();
    }
}
