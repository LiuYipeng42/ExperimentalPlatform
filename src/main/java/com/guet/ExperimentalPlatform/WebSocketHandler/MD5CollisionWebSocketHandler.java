package com.guet.ExperimentalPlatform.WebSocketHandler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.pojo.UserInfo;
import com.guet.ExperimentalPlatform.service.MD5CollisionService;
import com.guet.ExperimentalPlatform.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class MD5CollisionWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, Long> userIds = new ConcurrentHashMap<>();

    MD5CollisionService md5CollisionService;
    StudentService studentService;

    @Autowired
    public MD5CollisionWebSocketHandler(MD5CollisionService md5CollisionService, StudentService studentService){
        this.md5CollisionService = md5CollisionService;
        this.studentService = studentService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userAccount = (String) session.getAttributes().get("userAccount");
        String filePath = "MD5CollisionFiles/ExperimentDataFile/" + userIds.get(userAccount);

        String command = message.getPayload();
        String result = "";

        if (command.startsWith("make clean")){
            System.out.println("make clean -C " + filePath);
            result = RunCMD.runCMD("make clean -C " + filePath);
        }else if(command.startsWith("make")){
            System.out.println("make -C " + filePath);
            result = RunCMD.runCMD("make -C " + filePath);
        }else if(command.startsWith("./")){
            System.out.println("./" + filePath + "/" + command.substring(2));
            result = RunCMD.runCMD("./" + filePath + "/" + command.substring(2));
        }else if(command.startsWith("hex ")){
            System.out.println("hexdump -Cv " + filePath + "/" + command.substring(4));
            result = RunCMD.runCMD("hexdump -Cv " + filePath + "/" + command.substring(4));
        }else if(command.startsWith("ls")){
            System.out.println("ls " + filePath);
            result = RunCMD.runCMD("ls " + filePath);
        }else if(command.startsWith("vi ")){
            System.out.println("vi " + filePath + "/" + command.substring(3));
            result = FileOperation.readFile(filePath + "/" + command.substring(3));
        }

        System.out.println(result);
        session.sendMessage(new TextMessage(result));
        
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String userAccount = (String) session.getAttributes().get("userAccount");

        long userId = studentService.getOne(
                new QueryWrapper<Student>().eq("account", userAccount)
        ).getId();

        userIds.put(userAccount, userId);

        md5CollisionService.createEnvironment(
                String.valueOf(userIds.get(userAccount))
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        String userAccount = (String) session.getAttributes().get("userAccount");

        userIds.remove((String) session.getAttributes().get("userAccount"));

        md5CollisionService.closeEnvironment(
                String.valueOf(userIds.get(userAccount))
        );
    }

}