package com.guet.ExperimentalPlatform.controller;


import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.LoadForceContains;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("RunCode")
public class RunCodeController {

    private final String[] aesForceContains = LoadForceContains.load("CodeTest/FilesForCopy/aes.py");
    private final String[] rsaForceContains = LoadForceContains.load("CodeTest/FilesForCopy/rsa.py");

    @PostMapping("/runAes")
    public String runAes(HttpServletRequest request) throws IOException {
        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        FileOperation.savePostPythonFile(
                request,
                "CodeTest/AESRunningCodes/" +
                        userId +
                        ".py"
        );

        return RunPython.run(
                "CodeTest/AESRunningCodes/" + userId + ".py",
                new String[]{
                        "Cryptodome.Cipher:AES",
                        "binascii:b2a_hex",
                        "binascii:a2b_hex",
                        "traceback", "sys"
                },
                aesForceContains
        );

    }

    @PostMapping("/runRsa")
    public String runRsa(HttpServletRequest request) throws IOException {
        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        FileOperation.savePostPythonFile(
                request,
                "CodeTest/RSARunningCodes/" +
                        userId +
                        ".py"
        );

        return RunPython.run(
                "CodeTest/RSARunningCodes/" + userId + ".py",
                new String[]{
                        "Cryptodome.PublicKey:RSA",
                        "Cryptodome.Cipher:PKCS1_v1_5",
                        "Cryptodome.Hash:SHA256",
                        "Crypto.Signature:PKCS1_v1_5",
                        "base64", "random", "traceback", "sys"
                },
                rsaForceContains
        );

    }

}
