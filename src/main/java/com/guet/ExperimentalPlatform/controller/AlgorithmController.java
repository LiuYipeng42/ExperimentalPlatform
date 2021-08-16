package com.guet.ExperimentalPlatform.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.entity.AlgorithmParams;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/server")
public class AlgorithmController {

    @PostMapping("/aes")
    public JSONObject Aes(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return JSON.parseObject(
                RunCMD.runCMD("python3 src/main/resources/Algorithms/AES.py "
                + algorithmPara.getParam1() + " " + algorithmPara.getParam2())
        );
    }

    @PostMapping("/aesice/plaintext")
    public JSONObject getPlaintext(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return JSON.parseObject(
                RunCMD.runCMD(
                        "python3 src/main/resources/Algorithms/AES_ice.py encryption_plaintext "
                                + algorithmPara.getParam1() + " " + algorithmPara.getParam2()
                )
        );
    }

    @PostMapping("/aesice/secret")
    public JSONObject getSecretKey(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return JSON.parseObject(
                RunCMD.runCMD("python3 src/main/resources/Algorithms/AES_ice.py encryption_secret_key "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2())
        );
    }

    @PostMapping("/aes/decryption")
    public String AESDecryption(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/AES_For_Result.py decryption "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2());
    }

    @PostMapping("/aes/encryption")
    public String AESEncryption(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/AES_For_Result.py encryption "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2());
    }

    @PostMapping("/caesar")
    public String Caesar(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/Caesar.py "
                + algorithmPara.getParam1() + " " + algorithmPara.getParam2());
    }

    @PostMapping("/rsad")
    public String getRSAd(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/get_RSA_d.py "
                + algorithmPara.getP() + " " + algorithmPara.getQ() + " " + algorithmPara.getE());
    }

    @PostMapping("/rsaCalculateC")
    public String rsaCalculateC(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/rsa_calculate.py calculate_c "
                + algorithmPara.getM() + " " + algorithmPara.getE() + " " + algorithmPara.getN());
    }

    @PostMapping("/rsaCalculateM")
    public String rsaCalculateM(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/rsa_calculate.py calculate_m "
                + algorithmPara.getC() + " " + algorithmPara.getD() + " " + algorithmPara.getN());
    }

    @PostMapping("/hash")
    public String hash(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.runCMD("python3 src/main/resources/Algorithms/Hash.py "
                + algorithmPara.getParam1() + " " + algorithmPara.getParam2());
    }

}
