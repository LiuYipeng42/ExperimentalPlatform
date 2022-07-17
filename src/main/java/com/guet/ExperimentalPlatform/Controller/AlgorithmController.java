package com.guet.ExperimentalPlatform.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.pojo.AlgorithmParams;
import com.guet.ExperimentalPlatform.Entity.AlgorithmRecord;
import com.guet.ExperimentalPlatform.Service.AlgorithmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/server")
public class AlgorithmController {

    private final AlgorithmRecordService algorithmRecordService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public AlgorithmController(AlgorithmRecordService algorithmRecordService, RedisTemplate<String, Object> redisTemplate){
        this.algorithmRecordService = algorithmRecordService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/sha256")
    public JSONObject sha256(@RequestParam("plaintext") String plaintext) throws IOException {
        return JSON.parseObject(
                RunCMD.execute("python3 Algorithms/SHA256.py " + plaintext)
        );
    }

    @PostMapping("/aes") // aes 加密过程
    public JSONObject Aes(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("aesProcedure").setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return JSON.parseObject(
                RunCMD.execute("python3 Algorithms/AES.py "
                + algorithmPara.getParam1() + " " + algorithmPara.getParam2())
        );
    }

    @PostMapping("/aesice/plaintext") // aes 更改明文比较
    public JSONObject getPlaintext(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("aesAvalanchePlaintext").setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        String result = RunCMD.execute(
                "python3 Algorithms/AES_ice.py different_plaintext "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2()
        );

        System.out.println(result);
        return JSON.parseObject(result);
    }

    @PostMapping("/aesice/secret") // aes 更改密文比较
    public JSONObject getSecretKey(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("aesAvalancheSecretKey").setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return JSON.parseObject(
                RunCMD.execute("python3 Algorithms/AES_ice.py different_key "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2())
        );
    }

    @PostMapping("/aes/decryption") // aes 解密
    public String AESDecryption(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("aesDecryption-" + algorithmPara.getType()).setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return RunCMD.execute("python3 Algorithms/AES_For_Result.py decryption "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2() + " " + algorithmPara.getType());
    }

    @PostMapping("/aes/encryption") // aes 加密
    public String AESEncryption(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("aesEncryption-" + algorithmPara.getType()).setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return RunCMD.execute("python3 Algorithms/AES_For_Result.py encryption "
                        + algorithmPara.getParam1() + " " + algorithmPara.getParam2() + " " + algorithmPara.getType());
    }

    @PostMapping("/caesar")
    public String Caesar(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.execute("python3 Algorithms/Caesar.py "
                + algorithmPara.getParam1() + " " + algorithmPara.getParam2());
    }

    @PostMapping("/rsad")
    public String getRSAd(@RequestBody AlgorithmParams algorithmPara) throws IOException {

        return RunCMD.execute("python3 Algorithms/get_RSA_d.py "
                + algorithmPara.getP() + " " + algorithmPara.getQ() + " " + algorithmPara.getE());
    }

    @PostMapping("/rsaCalculateC")  // rsa 加密
    public String rsaCalculateC(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("rsaEncryption").setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return RunCMD.execute("python3 Algorithms/rsa_calculate.py calculate_c "
                + algorithmPara.getM() + " " + algorithmPara.getE() + " " + algorithmPara.getN());
    }

    @PostMapping("/rsaCalculateM")  // rsa 解密
    public String rsaCalculateM(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("rsaDecryption").setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return RunCMD.execute("python3 Algorithms/rsa_calculate.py calculate_m "
                + algorithmPara.getC() + " " + algorithmPara.getD() + " " + algorithmPara.getN());
    }

    @PostMapping("/hash")  // 5 种 hash
    public String hash(HttpServletRequest request, @RequestBody AlgorithmParams algorithmPara) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm(algorithmPara.getParam2()).setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return RunCMD.execute("python3 Algorithms/Hash.py "
                + algorithmPara.getParam1() + " " + algorithmPara.getParam2());
    }

    @GetMapping("/md5")
    public String md5(HttpServletRequest request, @RequestParam("plaintext") String plaintext) throws IOException {

        try {

            long userId = (long) request.getSession().getAttribute("userId");

            algorithmRecordService.save(
                    new AlgorithmRecord().setAlgorithm("md5").setStudentId(userId)
            );
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } catch (Exception ignored){
        }

        return RunCMD.execute("python3 Algorithms/MD5_Final_Edition.py " + plaintext);
    }

}
