package com.guet.ExperimentalPlatform.WebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ExperimentalPlatform.Utils.CodeSimilarity;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.service.MD5CollisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;


@Component
public class MD5CollisionWebSocketHandler extends TextWebSocketHandler {

    private final MD5CollisionService md5CollisionService;

    @Autowired
    public MD5CollisionWebSocketHandler(MD5CollisionService md5CollisionService) {
        this.md5CollisionService = md5CollisionService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        long userId = (long) session.getAttributes().get("userId");
        String filePath = "MD5CollisionFiles/ExperimentDataFile/" + userId;

        String command = message.getPayload();
        String result = "";

        if (!command.startsWith("./")) {

            if (command.startsWith("make clean")) {
                result = RunCMD.execute("make clean -C " + filePath);
            } else if (command.startsWith("make")) {
                result = RunCMD.execute("make -C " + filePath);
            } else if (command.startsWith("hex ")) {
                result = RunCMD.execute("hexdump -Cv " + filePath + "/" + command.substring(4));
            } else if (command.startsWith("ls")) {
                result = RunCMD.execute("ls " + filePath);
            } else if (command.startsWith("vi ")) {
                result = FileOperation.readFile(filePath + "/" + command.substring(3));
            } else if (command.startsWith("echo ")) {
                result = RunCMD.execute(command, filePath);
            } else if (command.startsWith("saveFile ")) {
                JSONObject messageJsonObject = JSON.parseObject(command.substring(8));
                FileOperation.writeFile(filePath + "/" + messageJsonObject.getString("fileName"),
                        messageJsonObject.getString("data"));
            } else {
                result = command.split(" ")[0] + ": " + "command not fund\n";
            }

            session.sendMessage(new TextMessage(result));
        }

        if (command.startsWith("./")) {

            double codeSimilarity = 0;

            if (command.substring(2).equals("task3.sh") || command.substring(2).contains("task4-")){
                codeSimilarity = CodeSimilarity.calculate(
                        FileOperation.readFile("MD5CollisionFiles/OriginalFiles/" + command.substring(2)),
                        FileOperation.readFile(filePath + "/" + command.substring(2))
                );
            }

            if (codeSimilarity < 0.91) {
                RunCMD.execute("./" + command.substring(2), filePath, session);
            }else {
                session.sendMessage(new TextMessage("代码修改过多"));
            }

        }

        System.out.println("----------------------------------------------------");
        System.out.println(userId +  " " + new Date() +  " " + command);
        System.out.println(result);
        System.out.println("----------------------------------------------------");

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        long userId = (long) session.getAttributes().get("userId");

        md5CollisionService.createEnvironment(
                String.valueOf(userId)
        );

        System.out.println("----------------------------------------------------");
        System.out.println(userId +  " " + new Date() +  " Create MD5 Collision environment");
        System.out.println("----------------------------------------------------");

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        long userId = (long) session.getAttributes().get("userId");

        md5CollisionService.closeEnvironment(String.valueOf(userId));

    }

}