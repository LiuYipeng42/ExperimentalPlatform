package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class RunCMD {
    public static String runPython(String filePath) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec("python3 " + filePath);

        return getResult(process);
    }

    public static String runCMD(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec(command);

        return getResult(process);
    }

    public static String getResult(Process process) throws IOException {
        LineNumberReader input = new LineNumberReader (new InputStreamReader(process.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = input.readLine()) != null){
            result.append(line).append("\n");
        }
        return result.toString();
    }

}
