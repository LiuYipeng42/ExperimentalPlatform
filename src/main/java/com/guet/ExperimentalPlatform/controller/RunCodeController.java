package com.guet.ExperimentalPlatform.controller;


import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.LoadForceContains;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import com.guet.ExperimentalPlatform.pojo.RunCodeResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

@CrossOrigin
@RestController
@RequestMapping("RunCode")
public class RunCodeController {

    private static final String[] aesForceContains = LoadForceContains.load("CodeTest/FilesForCopy/aes.py");
    private static final String[] rsaForceContains = LoadForceContains.load("CodeTest/FilesForCopy/rsa.py");
    private static final String[] hashForceContains = LoadForceContains.load("CodeTest/FilesForCopy/hash.py");

    private static final String aesRightAnswer =
            "b'be96f99ba8b3331d896c2aa398a41aa3ae81db1a49b2ac6cf92976f97083184e'\n" +
            "桂林电子科技大学\n" +
            "b'02cbef63143d1cfec26d1019bda98d652ff09b246df9061cddf1a357662dbdc8'\n" +
            "桂林电子科技大学\n";

    private static final String hashRightAnswer =
            "29487b3263304dba8c04fbe1169a8b8044e0bf8e\n" +
            "51a2141f13aaeec8af96051bc91567b1e2c24a8ff480b87cd963e0ef\n" +
            "f0437c34a0c3005abce3733f5c180845b0df755be4515665f2c6dbe0c12184a7\n" +
            "cff7f9ed424bd10768c546d6ea63fbd79a90e9fd2e24d895383777d6db92f06243106a34766a4b9ee35e41252ca34736\n" +
            "a1937495f4f2bfb4398a4ee4dbf63586631bcbca358416ebd42d63f80bcdac6fa4882f6d81b4480d3b3c7a41625033f3545b8472f02be0626d65fa790f6d549a\n";

    @GetMapping("getCode/{fileName}")
    public String getCode(HttpServletRequest request, @PathVariable("fileName") String fileName){
        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));
        String codePath = "CodeTest/" + fileName + "RunningCodes/" + userId + ".py";

        if(!new File("CodeTest/" + fileName + "RunningCodes/" + userId + ".py").exists()){
            FileOperation.copyFile(
                    "CodeTest/FilesForCopy/" + fileName.toLowerCase(Locale.ROOT) + ".py",
                    codePath
            );
        }

        return FileOperation.readFile(codePath);
    }

    @PostMapping("/runAes")
    public RunCodeResult runAes(HttpServletRequest request) throws IOException {
        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        FileOperation.savePostPythonFile(
                request,
                "CodeTest/AESRunningCodes/" +
                        userId +
                        ".py"
        );

        RunCodeResult runCodeResult = new RunCodeResult();

        String result = RunPython.run(
                "CodeTest/AESRunningCodes/" + userId + ".py",
                new String[]{
                        "Cryptodome.Cipher:AES",
                        "binascii:b2a_hex",
                        "binascii:a2b_hex",
                        "traceback", "sys"
                },
                aesForceContains
        );

        runCodeResult.setResult(result);

        if (result.equals(aesRightAnswer)) {
            runCodeResult.setStatus("success");
        } else {
            runCodeResult.setStatus("fail");
        }

        return runCodeResult;

    }

    @PostMapping("/runRsa")
    public RunCodeResult runRsa(HttpServletRequest request) throws IOException {
        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        FileOperation.savePostPythonFile(
                request,
                "CodeTest/RSARunningCodes/" +
                        userId +
                        ".py"
        );

        RunCodeResult runCodeResult = new RunCodeResult();

        String result = RunPython.run(
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

        runCodeResult.setResult(result);
        String[] answer = Arrays.stream(result.split("\n")).filter(x->x.contains("明文")).toArray(String[]::new);

        if (answer.length == 2 && answer[0].split("：")[1].equals(answer[1].split("：")[1])) {
            runCodeResult.setStatus("success");
        } else {
            runCodeResult.setStatus("fail");
        }

        return runCodeResult;
    }

    @PostMapping("/runHash")
    public RunCodeResult runHash(HttpServletRequest request) throws IOException {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        FileOperation.savePostPythonFile(
                request,
                "CodeTest/HashRunningCodes/" +
                        userId +
                        ".py"
        );

        RunCodeResult runCodeResult = new RunCodeResult();

        String result = RunPython.run(
                "CodeTest/HashRunningCodes/" + userId + ".py",
                new String[]{"hashlib", "traceback", "sys"},
                hashForceContains
        );

        runCodeResult.setResult(result);

        if (result.equals(hashRightAnswer)) {
            runCodeResult.setStatus("success");
        } else {
            runCodeResult.setStatus("fail");
        }

        return runCodeResult;

    }

}
