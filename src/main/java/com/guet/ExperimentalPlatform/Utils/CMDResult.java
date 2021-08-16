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


    public static void getResult(Process process, WebSocketSession session, long timeOut){

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
        int lastLoop = 0;
        double count = 0;
        int second = 0;

        try {
            while (lastLoop < 2){

                lastResult = result;

                if (resultThread == null || !resultThread.isAlive()) {
                    if (futureTask != null) {
                        result = futureTask.get();
                        session.sendMessage(new TextMessage(result));
                    }
                    futureTask = new FutureTask<>(new ProcessResult(process));
                    resultThread = new Thread(futureTask);
                    resultThread.start();
                }

                if (result.equals(lastResult)) {
                    count += 0.1;
                    if (count >= 1){
                        count = 0;
                        second += 1;
                        session.sendMessage(new TextMessage("running " + second + " seconds\n"));
                    }
                }

                if (!process.isAlive()){
                    lastLoop ++;
                }

                Thread.sleep(100);
            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }

    }

}

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
