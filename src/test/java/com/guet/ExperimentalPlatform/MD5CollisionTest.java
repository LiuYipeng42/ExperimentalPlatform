package com.guet.ExperimentalPlatform;

import com.guet.ExperimentalPlatform.Utils.RunCMD;

import java.io.IOException;

public class MD5CollisionTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(RunCMD.runCMD("hexdump -Cv src/main/resources/application.yml"));
    }
}
