package com.guet.ExperimentalPlatform.config;

import com.guet.ExperimentalPlatform.Interceptor.WebSocketHandshakeInterceptor;
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

    @Autowired
    public void setTransmissionWebSocketHandler(TransmissionWebSocketHandler transmissionWebSocketHandler) {
        this.transmissionWebSocketHandler = transmissionWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        webSocketHandlerRegistry.addHandler(this.transmissionWebSocketHandler, "/fileTransmissionServer")  //相等于@ServerEndPoin
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*") ;//跨域支持;//添加拦截器

    }
}
