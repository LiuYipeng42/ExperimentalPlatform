package com.guet.ExperimentalPlatform.Utils;

import java.io.IOException;

public class RunCMD {

    public static String runCMD(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec(command);

        return CMDResult.getResult(process);
    }

}
