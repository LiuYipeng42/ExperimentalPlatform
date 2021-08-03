package com.guet.ExperimentalPlatform;


import com.guet.ExperimentalPlatform.Utils.RunPython;

import java.io.IOException;


public class LimitPythonEditTest {

    public static void main(String[] args) throws IOException {
        System.out.println(RunPython.runPython("PaddingOracleFiles/ExperimentDataFile/10_manual_attack.py"));
    }

}
