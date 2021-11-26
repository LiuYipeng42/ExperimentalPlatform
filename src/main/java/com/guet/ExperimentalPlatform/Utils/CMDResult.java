package com.guet.ExperimentalPlatform.Utils;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class CMDResult {

    public static String getResult(Process process, long timeOut) throws IOException {

        if (timeOut > 0) {
            new Thread(
                    () -> {
                        try {
                            Thread.sleep(timeOut * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        process.destroy();
                    }
            ).start();
        }

        LineNumberReader input = new LineNumberReader(new InputStreamReader(process.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = input.readLine()) != null) {
            result.append(line).append("\n");
        }

        return result.toString();
    }

    public static void getResult(Process process, long timeOut, WebSocketSession session) {

        // 计时器线程
        if (timeOut > 0) {
            new Thread(
                    () -> {
                        try {
                            Thread.sleep(timeOut * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        process.destroy();
                    }
            ).start();
        }

        Thread resultThread = null;
        FutureTask<String> futureTask = null;
        String lastResult;
        String result = "";
        long start = System.currentTimeMillis();
        long t1 = start;
        long t2;

        try {

            while (true) {

                lastResult = result;

                // 若获取结果的线程结束，则再次创建
                if (resultThread == null || !resultThread.isAlive()) {
                    if (futureTask != null) {
                        result = futureTask.get();
                        System.out.println(result);
                        session.sendMessage(new TextMessage(result));
                    }
                    futureTask = new FutureTask<>(new ProcessResult(process));
                    resultThread = new Thread(futureTask);
                    resultThread.start();
                }

                // 当前程序输出结果若与上次程序输出的结果相同，则表明程序此时正在运行，结果还没有输出
                if (result.equals(lastResult)) {
                    t2 = System.currentTimeMillis();
                    if (t2 - t1 >= 1000){
                        session.sendMessage(new TextMessage("running " + (t2 - start) / 1000 + " seconds\n"));
                        t1 = t2;
                    }
                }

                if (!process.isAlive()) {
                    break;
                }

                Thread.sleep(200);
            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }


}


// 用于获取运行结果
class ProcessResult implements Callable<String> {

    private final Process process;

    public ProcessResult(Process process){
        this.process = process;
    }

    @Override
    public String call(){
        LineNumberReader input = new LineNumberReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();

        String line;

        while (true) {
            try {

                line = input.readLine();

                // 若某一行为空，则结束线程
                if (line == null || line.length() == 0) {
                    break;
                }
                result.append(line).append("\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }
}
