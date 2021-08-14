package com.guet.ExperimentalPlatform.Interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    StudentService studentService;

    @Autowired
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                   ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler,
                                   Map<String, Object> map) {

        String userAccount = ((ServletServerHttpRequest) serverHttpRequest)
                .getServletRequest()
                .getParameter("userAccount");

        System.out.println(studentService);
        long userId = studentService.getOne(
                new QueryWrapper<Student>().eq("account", userAccount)
        ).getId();

        map.put("userId", userId);
        map.put("userAccount", userAccount);
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
