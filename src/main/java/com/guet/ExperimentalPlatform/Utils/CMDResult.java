package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CMDResult {
    public static String getResult(Process process) throws IOException {
        LineNumberReader input = new LineNumberReader(new InputStreamReader(process.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line;

        new Thread(
                ()->{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    process.destroy();
                }
        ).start();

        while ((line = input.readLine()) != null) {
            result.append(line).append("\n");
        }
        process.destroy();
        return result.toString();
    }
}
