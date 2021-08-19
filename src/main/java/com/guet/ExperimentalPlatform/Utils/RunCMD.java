package com.guet.ExperimentalPlatform.Utils;

import org.springframework.web.socket.WebSocketSession;

import java.io.*;

public class RunCMD {

    public static void execute(String command, String path, WebSocketSession session) throws IOException {

        Runtime runtime = Runtime.getRuntime();

        String[] cmd = {"/bin/sh", "-c", "cd " + path + ";" + command};

        Process process;

        process = runtime.exec(cmd);
        CMDResult.getResult(process, session, -1);

    }

    public static String execute(String command, String path) throws IOException {

        Runtime runtime = Runtime.getRuntime();

        String[] cmd = {"/bin/sh", "-c", "cd " + path + ";" + command};

        Process process;

        process = runtime.exec(cmd);

        return CMDResult.getResult(process, -1);
    }

    public static String execute(String command, int timeOut) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec(command);

        return CMDResult.getResult(process, timeOut);
    }

    public static String execute(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec(command);

        return CMDResult.getResult(process, -1);
    }

}
