package com.guet.ExperimentalPlatform.WebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Utils.CodeSimilarity;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.service.MD5CollisionService;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
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
    private final StudyRecordService studyRecordService;

    @Autowired
    public MD5CollisionWebSocketHandler(MD5CollisionService md5CollisionService,
                                        StudyRecordService studyRecordService) {
        this.md5CollisionService = md5CollisionService;
        this.studyRecordService = studyRecordService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        long userId = (long) session.getAttributes().get("userId");
        String filePath = "MD5CollisionFiles/ExperimentDataFile/" + userId;

        String command = message.getPayload();
        String result = "";

        if (command.startsWith("make clean")) {
            result = RunCMD.runCMD("make clean -C " + filePath);
        } else if (command.startsWith("make")) {
            result = RunCMD.runCMD("make -C " + filePath);
        } else if (command.startsWith("hex ")) {
            result = RunCMD.runCMD("hexdump -Cv " + filePath + "/" + command.substring(4));
        } else if (command.startsWith("ls")) {
            result = RunCMD.runCMD("ls " + filePath);
        } else if (command.startsWith("vi ")) {
            result = FileOperation.readFile(filePath + "/" + command.substring(3));
        } else if (command.startsWith("saveFile ")) {
            JSONObject messageJsonObject = JSON.parseObject(command.substring(8));
            FileOperation.writeFile(filePath + "/" + messageJsonObject.getString("fileName"),
                    messageJsonObject.getString("data"));
        } else if (command.startsWith("./")) {
            result = RunCMD.runCMD("./" + command.substring(2), filePath);

//            CodeSimilarity.calculate()

        } else if (command.startsWith("echo ")) {
            result = RunCMD.runCMD(command, filePath);
        } else {
            result = command.split(" ")[0] + ": " + "command not fund\n";
        }

        System.out.println("----------------------------------------------------");
        System.out.println(userId +  " " + new Date() +  " " + command);
        System.out.println(result);
        System.out.println("----------------------------------------------------");

        session.sendMessage(new TextMessage(result));

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        long userId = (long) session.getAttributes().get("userId");

        studyRecordService.save(
                new StudyRecord()
                        .setStudentId(userId)
                        .setStartTime(new Date())
                        .setExperimentType(3)
        );

        md5CollisionService.createEnvironment(
                String.valueOf(userId)
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        long userId = (long) session.getAttributes().get("userId");

        md5CollisionService.closeEnvironment(String.valueOf(userId));

        studyRecordService.update(
                null,
                new UpdateWrapper<StudyRecord>().set("end_time", new Date())
                        .eq("student_id", userId)
                        .eq("experiment_type", 3)
                        .isNull("end_time")
        );

    }

}