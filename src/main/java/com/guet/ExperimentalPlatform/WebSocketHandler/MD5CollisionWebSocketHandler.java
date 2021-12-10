package com.guet.ExperimentalPlatform.WebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Utils.*;
import com.guet.ExperimentalPlatform.Entity.MD5TaskRecord;
import com.guet.ExperimentalPlatform.pojo.MD5CommandResult;
import com.guet.ExperimentalPlatform.Service.MD5CollisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class MD5CollisionWebSocketHandler extends TextWebSocketHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    private final MD5CollisionService md5CollisionService;

    private static final HashMap<Long, String> userTask = new HashMap<>();
    // 记录用户每个任务运行的命令及其结果
    private static final HashMap<Long, List<MD5CommandResult>> userCommandsRecord = new HashMap<>();
    private static final HashMap<String, String[]> forceContains = new HashMap<>();
    // 存储实验中每个任务的目标
    private static final HashMap<String, String> taskTargets = new HashMap<>();

    static {
        forceContains.put("./task3.sh", LoadForceContains.load("MD5CollisionFiles/OriginalFiles/task3.sh"));
        forceContains.put("./task4-1.sh", LoadForceContains.load("MD5CollisionFiles/OriginalFiles/task4-1.sh"));
        forceContains.put("./task4-2.sh", LoadForceContains.load("MD5CollisionFiles/OriginalFiles/task4-2.sh"));

        String[] readme = FileOperation.readFile("MD5CollisionFiles/OriginalFiles/readme")
                .split("-------------------------------------------------------" +
                        "-----------------------------------------------------------");
        String[] tasks = new String[]{"task1", "task2", "task3", "task4", "task5"};

        for (String task : tasks) {
            for (String target : readme) {
                if (target.contains(task)) {
                    taskTargets.put(task, target);
                }
            }
        }

    }

    @Autowired
    public MD5CollisionWebSocketHandler(RedisTemplate<String, Object> redisTemplate, MD5CollisionService md5CollisionService) {
        this.redisTemplate = redisTemplate;
        this.md5CollisionService = md5CollisionService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        long userId = (long) session.getAttributes().get("userId");
        String command = message.getPayload().strip();

        if (command.equals("exit")) {
            userTask.remove(userId);
        }

        if (userTask.get(userId) != null && command.equals("finish")) {

            String checkResult = MD5FinishTask.judge(userId, userTask.get(userId), userCommandsRecord.get(userId));

            if (checkResult.equals("成功完成任务")) {
                md5CollisionService.update(
                        new UpdateWrapper<MD5TaskRecord>()
                                .set("end_time", new Date()).set("status", "finished")
                                .eq("student_id", userId)
                                .eq("task_name", userTask.get(userId))
                                .isNull("end_time")
                );
                redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
            }

            session.sendMessage(new TextMessage(checkResult));
        }

        if (userTask.get(userId) == null) {
            if (command.equals("task1") || command.equals("task2")
                    || command.equals("task3") || command.equals("task4") || command.equals("task5")) {
                userTask.put(userId, command);
                userCommandsRecord.put(userId, new ArrayList<>());

                if (md5CollisionService.getOne(
                        new QueryWrapper<MD5TaskRecord>().eq("student_id", userId).eq("task_name", command)
                ) == null) {

                    md5CollisionService.save(
                            new MD5TaskRecord()
                                    .setStudentId(userId)
                                    .setTaskName(command)
                                    .setStartTime(new Date())
                                    .setStatus("unfinished")
                    );
                }
                session.sendMessage(
                        new TextMessage(
                                "正在进行 " + command + "，可以通过运行 exit 退出当前任务\n" +
                                        "若任务完成，可以运行 finish 命令进行任务完成的检验\n" +
                                        taskTargets.get(command)
                        )
                );
            } else {
                session.sendMessage(new TextMessage(selectTaskText(userId)));
            }
        } else if (!(command.equals("exit") || command.equals("finish"))) {
            parseAndRunCommand(session, command, userId);
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        long userId = (long) session.getAttributes().get("userId");

        md5CollisionService.createEnvironment(
                String.valueOf(userId)
        );

        String text = selectTaskText(userId);

        System.out.println(text);

        session.sendMessage(new TextMessage(text));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {

        long userId = (long) session.getAttributes().get("userId");

        redisTemplate.opsForValue().setBit("runningMD5", userId, false);

        userTask.remove(userId);
        userCommandsRecord.remove(userId);

        md5CollisionService.closeEnvironment(String.valueOf(userId));

    }

    private void parseAndRunCommand(WebSocketSession session, String command, long userId) throws IOException {

        if (!command.startsWith("saveFile") && !command.startsWith("md5collgen") && !command.startsWith("fastcoll")) {

            if (!command.contains("sh") && !command.contains("fastcoll ")
                    && (command.contains("/") || command.contains("-"))) {
                session.sendMessage(new TextMessage("不可运行"));
                return;
            }

            if (command.contains("rm ") || command.contains("&") || command.contains("|") || command.contains("<")) {
                session.sendMessage(new TextMessage("不可运行"));
                return;
            }

            if (command.startsWith("./") && command.contains("sh")) {
                if (!command.equals("./task3.sh") && !command.equals("./task4-1.sh") && !command.equals("./task4-2.sh")) {
                    session.sendMessage(new TextMessage("不可运行 task3.sh, task4-1.sh 和 task4-2.sh 以外的sh文件"));
                    return;
                }
            }
        }

        String filePath = "MD5CollisionFiles/ExperimentDataFile/" + userId;

        String result = "";
        double codeSimilarity;

        List<String> cdCommands = Arrays.asList("cat", "echo", "sha256sum", "md5sum", "xxd", "make", "ls");

        if (!command.startsWith("./")) {

            if (command.startsWith("make clean")) {
                result = RunCMD.execute("make clean -C " + filePath);
            } else if (command.startsWith("hex ")) {
                result = RunCMD.execute("hexdump -Cv " + filePath + "/" + command.substring(4));
            } else if (command.startsWith("vi ")) {

                String fileName = command.substring(3);
                File file = new File(filePath + "/" + fileName);

                if(file.exists()) {
                    result = FileOperation.readFile(filePath + "/" + fileName);
                }else {
                    FileOperation.writeFile(filePath + "/" + fileName, "\n");
                    result = "\n";
                }

            } else if (cdCommands.contains(command.split(" ")[0])) {
                result = RunCMD.execute(command, filePath);

                if (command.startsWith("echo") || command.startsWith("cat")) {
                    result = "'" + command + "' 运行成功";
                }
            } else if (command.startsWith("fastcoll ")) {

                runMD5Collgen("./" + command, filePath, session, userId);

            } else if (command.startsWith("md5collgen ")) {

                runMD5Collgen("./" + command, filePath, session, userId);

            } else if (command.startsWith("python3 ")) {

                result = RunPython.run(filePath + "/" + command.split(" ")[1], new String[]{"sys"});

            }  else if (command.startsWith("saveFile ")) {

                String data;

                JSONObject messageJsonObject = JSON.parseObject(command.substring(8));
                data = messageJsonObject.getString("data");

                if (data.contains("rm ")) {
                    return;
                }

                String fileName = messageJsonObject.getString("fileName");

                if (fileName.equals("out1.py") || fileName.equals("out2.py")){
                    session.sendMessage(new TextMessage("不可创建 out1.py 和 out2.py 文件"));
                }

                System.out.println(fileName);
                FileOperation.writeFile(
                        filePath + "/" + messageJsonObject.getString("fileName"), data
                );

            } else {
                result = command.split(" ")[0] + ": " + "command not fund\n";
            }

            session.sendMessage(new TextMessage(result));
        }

        if (command.startsWith("./")) {

            if (!new File(filePath + "/" + command.substring(2)).exists()) {
                session.sendMessage(new TextMessage(command.substring(2) + ": " + "no such file\n"));
                return;
            }

            if (command.startsWith("./task3.sh") || command.startsWith("./task4-")) {

                for (String s : forceContains.get(command)) {
                    if (!FileOperation.readFile(filePath + "/" + command.substring(2)).contains(s)) {
                        session.sendMessage(new TextMessage("不可修改虚线外的代码"));
                        return;
                    }
                }

                codeSimilarity = CodeSimilarity.calculate(
                        FileOperation.readFile("MD5CollisionFiles/OriginalFiles/" + command.substring(2)),
                        FileOperation.readFile(filePath + "/" + command.substring(2))
                );
                System.out.println(codeSimilarity);

                if (codeSimilarity == 1.0) {
                    session.sendMessage(new TextMessage("没有修改代码"));
                } else {
                    if ((command.startsWith("./task3.sh") && codeSimilarity > 0.91) ||
                            (command.startsWith("./task4-1.sh") && codeSimilarity > 0.98) ||
                            (command.startsWith("./task4-2.sh") && codeSimilarity > 0.85)) {

                        runMD5Collgen(command, filePath, session, userId);

                    } else {
                        session.sendMessage(new TextMessage("代码修改过多"));
                    }
                }
            } else {
                result = RunCMD.execute(command, filePath);
                session.sendMessage(new TextMessage(result));
            }

        }

        List<String> checkCommands =
                Arrays.asList("sha256sum", "md5sum", "xxd", "md5collgen",
                        "./a1.out", "./a2.out", "hextask3", "vitask3.sh", "./task3.sh",
                        "hextask4", "vitask4-1.sh", "./task4-1.sh",
                        "vitask4-2.sh", "./task4-2.sh",
                        "python3out1.py", "python3out2.py", "hexout1.py", "hexout2.py");

        if (!result.equals(command.split(" ")[0] + ": " + "command not fund\n")) {
            if (checkCommands.contains(command.split(" ")[0]) ||
                    checkCommands.contains(command.replace(" ", ""))) {
                System.out.println();
                List<MD5CommandResult> results = userCommandsRecord.get(userId);
                results.add(new MD5CommandResult(command, result));
                userCommandsRecord.put(userId, results);
            }
        }

        System.out.println("----------------------------------------------------");
        System.out.println(userId + " " + new Date() + " " + command);
        if (result.length() > 1024) {
            System.out.println(result.substring(0, 1024));
        } else {
            if (result.length() > 200) {
                System.out.println(result.substring(0, 200));
            } else {
                System.out.println(result);
            }
        }
        System.out.println("----------------------------------------------------");
    }

    private void runMD5Collgen(String command, String filePath, WebSocketSession session, long userId) throws IOException {
        // 有并发安全性的问题

        // 限制只有 5 个用户可以同时运行 md5collgen 命令
        if (RedisOps.bitCount(redisTemplate, "runningMD5").longValue() <= 5) {
            // 同一个用户同时只能运行一次
            if (!Boolean.TRUE.equals(redisTemplate.opsForValue().getBit("runningMD5", userId))) {

                new Thread(() -> {
                    System.out.println(userId + " running " + RedisOps.bitCount(redisTemplate, "runningMD5"));
                    redisTemplate.opsForValue().setBit("runningMD5", userId, true);
                    try {
                        RunCMD.execute(command, filePath, session);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    redisTemplate.opsForValue().setBit("runningMD5", userId, false);
                    System.out.println(userId + " stop " + RedisOps.bitCount(redisTemplate, "runningMD5"));

                }).start();

            } else {
                session.sendMessage(new TextMessage(command + "正在运行"));
            }
        } else {
            session.sendMessage(new TextMessage("此命令需要使用大量CPU资源，由于服务器性能原因，此时不可运行，请稍后再试"));
        }
    }

    private String selectTaskText(long userId) {
        List<String> finishedTasks = md5CollisionService.list(
                new QueryWrapper<MD5TaskRecord>()
                        .select("task_name")
                        .eq("student_id", userId)
                        .eq("status", "finished")
        ).stream().map(MD5TaskRecord::getTaskName).collect(Collectors.toList());

        StringBuilder text = new StringBuilder("请选择一个任务：");

        for (String task : new String[]{"task1", "task2", "task3", "task4", "task5"}) {
            if (finishedTasks.contains(task)) {
                text.append("\n    ").append(task).append(" √");
            } else {
                text.append("\n    ").append(task).append(" X");
            }
        }

        text.append("\n  (√: 已完成，X: 未完成，发送任务名称即可进入对应任务)");

        return text.toString();
    }

}