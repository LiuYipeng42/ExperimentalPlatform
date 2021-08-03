package com.guet.ExperimentalPlatform.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.socket.WebSocketSession;

@Data
@Accessors(chain = true)
public class WebSocketInfo {
    private long userId;
    private WebSocketSession session;
}
