package com.guet.ExperimentalPlatform.Utils;

import java.io.*;

public class RunCMD {

    public static String runCMD(String command, String path) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        String[] cmd = {"/bin/sh", "-c", "cd " + path + ";" + command};

        // cd MD5CollisionFiles/ExperimentDataFile/30;./task3.sh
        Process process = runtime.exec(cmd);

        try {
            process.waitFor();
        } catch (InterruptedException ignored){
        }

        return CMDResult.getResult(process, -1);
    }

    public static String runCMD(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec(command);

        try {
            process.waitFor();
        } catch (InterruptedException ignored){
        }
        return CMDResult.getResult(process, -1);
    }

}
