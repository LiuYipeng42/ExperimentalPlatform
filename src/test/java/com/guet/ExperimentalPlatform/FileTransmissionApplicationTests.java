package com.guet.ExperimentalPlatform;

import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.LoadForceContains;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;


//@RunWith(SpringJUnit4ClassRunner.class)
public class FileTransmissionApplicationTests {


    //    @Test
    public static void main(String[] args) throws IOException {
        String[] aesForceContains = LoadForceContains.load("CodeTest/FilesForCopy/aes.py");

        for (String s: aesForceContains) {
            System.out.println("——————————————————————————————————————————————————————————————————");
            System.out.println(s);
            System.out.println("——————————————————————————————————————————————————————————————————");
        }

        System.out.println(
                RunPython.run(
                        "CodeTest/AESRunningCodes/" + 10 + ".py",
                        new String[]{"AES", "b2a_hex", "a2b_hex", "traceback"},
                        aesForceContains
                )
        );
    }

}
