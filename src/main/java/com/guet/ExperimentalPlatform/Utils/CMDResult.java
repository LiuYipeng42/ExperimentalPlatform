package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CMDResult {
    public static String getResult(Process process, long timeOut) throws IOException {

        if (timeOut > 0) {
            new Thread(
                    () -> {
                        try {
                            Thread.sleep(timeOut);
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

//        process.destroy();
        return result.toString();
    }
}
