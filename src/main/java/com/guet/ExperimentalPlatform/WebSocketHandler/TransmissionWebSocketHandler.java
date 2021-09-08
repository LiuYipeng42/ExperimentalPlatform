package com.guet.ExperimentalPlatform.WebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ExperimentalPlatform.pojo.TransmissionInfo;
import com.guet.ExperimentalPlatform.Service.FileTransmissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TransmissionWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, Long> onlineUser = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> userSession = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TransmissionInfo> transmissionId = new ConcurrentHashMap<>();

    private final FileTransmissionService fileTransmissionService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TransmissionWebSocketHandler(FileTransmissionService fileTransmissionService, RedisTemplate<String, Object> redisTemplate) {
        this.fileTransmissionService = fileTransmissionService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        long userId = (long) session.getAttributes().get("userId");
        String userAccount = (String) session.getAttributes().get("userAccount");

        String messageJSON = message.getPayload();
        System.out.println(messageJSON);
        JSONObject messageJsonObject;
        String messageText;

        String toUserAccount;

        if (StringUtils.isNotBlank(messageJSON)) {
            try {
                //解析发送的报文
                messageJsonObject = JSON.parseObject(messageJSON);
                //追加发送人(防止串改)
                messageJsonObject.put("fromUserId", userAccount);

                toUserAccount = messageJsonObject.getString("toUserId").strip();
                messageText = messageJsonObject.getString("contentText");

                //传送给对应toUserAccount用户的websocket
                if (StringUtils.isNotBlank(toUserAccount) && userSession.containsKey(toUserAccount)) {
                    userSession.get(toUserAccount).sendMessage(new TextMessage(messageJsonObject.toJSONString()));
                } else {
                    System.out.println("请求的userAccount:" + toUserAccount + "不在该服务器上");
                }

                fileTransmissionService.saveMessage(
                        messageText,
                        userId,
                        onlineUser.get(toUserAccount),
                        transmissionId
                );

                redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        long userId = (long) session.getAttributes().get("userId");
        String userAccount = (String) session.getAttributes().get("userAccount");

        onlineUser.put(userAccount, userId);
        //加入set中
        userSession.remove(userAccount);
        userSession.put(userAccount, session);

        Collection<WebSocketSession> userSessions = userSession.values();

        for (WebSocketSession othersSession : userSessions) {
            try {
                if (othersSession != session) {
                    //通知其他用户，这个人上线了
                    othersSession.sendMessage(new TextMessage("online " + userAccount));
                } else {
                    //告诉自己有谁在线上
                    session.sendMessage(new TextMessage(onlineUser.keySet().toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

//        long userId = (long) session.getAttributes().get("userId");
        String userAccount = (String) session.getAttributes().get("userAccount");

        onlineUser.remove(userAccount);
        userSession.remove(userAccount);
        Collection<WebSocketSession> userSessions = userSession.values();
        for (WebSocketSession othersSession : userSessions) {
            try {
                //通知其他用户，这个人离线了
                othersSession.sendMessage(new TextMessage("offline " + userAccount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
