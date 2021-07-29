package com.guet.ExperimentalPlatform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.pojo.TransmissionInfo;
import com.guet.ExperimentalPlatform.service.MessageService;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
import com.guet.ExperimentalPlatform.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@ServerEndpoint("/imserver/{userId}")
@Component
@Slf4j
public class WebSocketServer {
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * ConcurrentHashMap是HashMap的一个线程安全的、支持高效并发的版本
     */
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TransmissionInfo> transmissionId = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    private String userAccount = "";
    private long userId;

    // 项目启动时初始化，会初始化 websocket （非用户连接的），spring 同时会为其注入 service，该对象的 service 不是 null，被成功注入。
    // 但是，由于 spring 默认管理的是单例(singleton)，所以只会注入一次 service。
    // 当新用户进入发送信息时，系统又会创建一个新的 websocket 对象，这时矛盾出现了：
    // spring 管理的都是单例，不会给第二个 websocket 对象注入 service，所以需要 static
    private static MessageService messageService;
    private static StudentService studentService;
    private static StudyRecordService studyRecordService;

    @Autowired
    public void setStudyRecordService(StudyRecordService studyRecordService) {
        WebSocketServer.studyRecordService = studyRecordService;
    }

    @Autowired
    public void setStudentService(StudentService studentService) {
        WebSocketServer.studentService = studentService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        WebSocketServer.messageService = messageService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userAccount) {

        this.session = session;
        this.userAccount = userAccount;

        this.userId = studentService.getOne(
                new QueryWrapper<Student>().eq("account", userAccount)
        ).getId();

        //        this.student = studentService.addLoginRecord(userAccount);
        studyRecordService.save(
                new StudyRecord().setStudentId(userId).setLoginTime(new Date())
        );

        if (webSocketMap.containsKey(userAccount)) {
            webSocketMap.remove(userAccount);
            webSocketMap.put(userAccount, this);
            //加入set中
        } else {
            webSocketMap.put(userAccount, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        Set<String> users = webSocketMap.keySet();
        log.info(users.toString());
        for (String user : users) {
            try {
                if (!user.equals(userAccount))  //通知其他用户，这个人上线了
                    webSocketMap.get(user).sendMessage("online" + userAccount);
                else    //告诉自己有谁在线上
                    webSocketMap.get(userAccount).sendMessage(users.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.info("用户连接:" + userAccount + ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.info("用户:" + userAccount + ",网络异常!!!!!!");
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userAccount)) {
            webSocketMap.remove(userAccount);
            //从set中删除
            subOnlineCount();
            Set<String> users = webSocketMap.keySet();
            for (String user : users) {
                try {
                    //通知其他用户，这个人离线了
                    webSocketMap.get(user).sendMessage("outline" + userAccount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("用户退出:" + userAccount + ",当前在线人数为:" + getOnlineCount());

        studyRecordService.update(
                null,
                new UpdateWrapper<StudyRecord>().set("logout_time", new Date())
                        .eq("student_id", userId)
                        .isNull("logout_time")
        );

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param messageJSON 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String messageJSON) {

        System.out.println(messageJSON);
        if (StringUtils.isNotBlank(messageJSON)) {
            try {
                //解析发送的报文
                JSONObject messageJsonObject = JSON.parseObject(messageJSON);
                //追加发送人(防止串改)
                messageJsonObject.put("fromUserId", this.userAccount);

                String messageText = messageJsonObject.getString("contentText");

                String toUserAccount = messageJsonObject.getString("toUserId");
                long toUserId = studentService.getOne(
                        new QueryWrapper<Student>().eq("account", toUserAccount)
                ).getId();

                messageService.saveMessage(messageText, userId, toUserId, transmissionId);

                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserAccount) && webSocketMap.containsKey(toUserAccount)) {
                    webSocketMap.get(toUserAccount).sendMessage(messageJsonObject.toJSONString());
                } else {
                    log.info("请求的userAccount:" + toUserAccount + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("用户错误:" + this.userAccount + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        // websocket  session发送文本消息
        // getAsyncRemote()和 getBasicRemote() 是异步与同步的区别
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}


