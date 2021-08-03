package com.guet.ExperimentalPlatform.config;

import com.guet.ExperimentalPlatform.Interceptor.WebSocketHandshakeInterceptor;
import com.guet.ExperimentalPlatform.WebSocketHandler.MD5CollisionWebSocketHandler;
import com.guet.ExperimentalPlatform.WebSocketHandler.TransmissionWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    TransmissionWebSocketHandler transmissionWebSocketHandler;
    MD5CollisionWebSocketHandler md5CollisionWebSocketHandler;

    @Autowired
    public WebSocketConfig(TransmissionWebSocketHandler transmissionWebSocketHandler,
                           MD5CollisionWebSocketHandler md5CollisionWebSocketHandler) {
        this.transmissionWebSocketHandler = transmissionWebSocketHandler;
        this.md5CollisionWebSocketHandler = md5CollisionWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        webSocketHandlerRegistry.addHandler(this.transmissionWebSocketHandler, "/fileTransmissionServer")  //相等于@ServerEndPoin
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        webSocketHandlerRegistry.addHandler(this.md5CollisionWebSocketHandler, "/md5CollisionServer")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

    }
}
