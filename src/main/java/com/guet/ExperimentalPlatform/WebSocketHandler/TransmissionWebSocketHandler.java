package com.guet.ExperimentalPlatform.WebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.pojo.TransmissionInfo;
import com.guet.ExperimentalPlatform.pojo.UserInfo;
import com.guet.ExperimentalPlatform.service.MessageService;
import com.guet.ExperimentalPlatform.service.StudentService;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TransmissionWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, UserInfo> userInfo = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TransmissionInfo> transmissionId = new ConcurrentHashMap<>();

    private final MessageService messageService;
    private final StudentService studentService;
    private final StudyRecordService studyRecordService;

    @Autowired
    private TransmissionWebSocketHandler(StudyRecordService studyRecordService,
                                         StudentService studentService,
                                         MessageService messageService) {
        this.studyRecordService = studyRecordService;
        this.messageService = messageService;
        this.studentService = studentService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        String messageJSON = message.getPayload();
        System.out.println(messageJSON);
        JSONObject messageJsonObject;
        String messageText;

        String userAccount = (String) session.getAttributes().get("userAccount");
        UserInfo userInfo = TransmissionWebSocketHandler.userInfo.get(userAccount);

        String toUserAccount;
        UserInfo toUserInfo;

        if (StringUtils.isNotBlank(messageJSON)) {
            try {
                //解析发送的报文
                messageJsonObject = JSON.parseObject(messageJSON);
                //追加发送人(防止串改)
                messageJsonObject.put("fromUserId", userAccount);

                toUserAccount = messageJsonObject.getString("toUserId").strip();
                toUserInfo = TransmissionWebSocketHandler.userInfo.get(toUserAccount);
                messageText = messageJsonObject.getString("contentText");

                messageService.saveMessage(messageText,
                        userInfo.getUserId(), toUserInfo.getUserId(),
                        transmissionId
                );

                //传送给对应toUserAccount用户的websocket
                if (StringUtils.isNotBlank(toUserAccount) && TransmissionWebSocketHandler.userInfo.containsKey(toUserAccount)) {
                    toUserInfo.getSession().sendMessage(new TextMessage(messageJsonObject.toJSONString()));
                } else {
                    System.out.println("请求的userAccount:" + toUserAccount + "不在该服务器上");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String userAccount = (String) session.getAttributes().get("userAccount");

        long userId = studentService.getOne(
                new QueryWrapper<Student>().eq("account", userAccount)
        ).getId();

        studyRecordService.save(
                new StudyRecord().setStudentId(userId).setLoginTime(new Date())
        );

        //加入set中
        userInfo.remove(userAccount);
        userInfo.put(userAccount, new UserInfo().setUserId(userId).setSession(session));

        Set<String> users = userInfo.keySet();

        for (String user : users) {
            try {
                if (!user.equals(userAccount)) {
                    //通知其他用户，这个人上线了
                    userInfo.get(user).getSession().sendMessage(new TextMessage("online " + userAccount));
                } else {
                    //告诉自己有谁在线上
                    userInfo.get(userAccount).getSession().sendMessage(new TextMessage(users.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        String userAccount = (String) session.getAttributes().get("userAccount");

        if (userInfo.containsKey(userAccount)) {
            userInfo.remove(userAccount);
            Set<String> users = userInfo.keySet();
            for (String user : users) {
                try {
                    //通知其他用户，这个人离线了
                    userInfo.get(user).getSession().sendMessage(new TextMessage("outline" + userAccount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        studyRecordService.update(
                null,
                new UpdateWrapper<StudyRecord>().set("logout_time", new Date())
                        .eq("student_id", userInfo.get(userAccount).getUserId())
                        .isNull("logout_time")
        );

    }

}
