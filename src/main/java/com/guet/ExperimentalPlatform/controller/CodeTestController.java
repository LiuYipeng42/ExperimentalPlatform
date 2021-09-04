package com.guet.ExperimentalPlatform.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.entity.CodeTestRecord;
import com.guet.ExperimentalPlatform.service.CodeTestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@CrossOrigin
@RestController
@RequestMapping("CodeTest")
public class CodeTestController {

    private final CodeTestRecordService codeTestRecordService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CodeTestController(CodeTestRecordService codeTestRecordService, RedisTemplate<String, Object> redisTemplate) {
        this.codeTestRecordService = codeTestRecordService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/start")
    public void startTest(HttpServletRequest request, @RequestParam("type") String codesType) {

        long userId = (long) request.getSession().getAttribute("userId");

        codeTestRecordService.remove(
                new QueryWrapper<CodeTestRecord>()
                        .eq("student_id", userId)
                        .eq("code_type", codesType)
                        .isNull("end_time")
        );

        if (codeTestRecordService.getOne(
                new QueryWrapper<CodeTestRecord>().eq("student_id", userId).eq("code_type", codesType)
        ) == null) {

            codeTestRecordService.save(
                    new CodeTestRecord().setStudentId(userId).setCodeType(codesType).setStartTime(new Date())
            );
        }

    }

    @GetMapping("/finish")
    public void finishTest(HttpServletRequest request, @RequestParam("type") String codesType) {
        long userId = (long) request.getSession().getAttribute("userId");

        codeTestRecordService.update(
                new UpdateWrapper<CodeTestRecord>()
                        .set("end_time", new Date())
                        .eq("student_id", userId)
                        .eq("code_type", codesType)
                        .isNull("end_time")
        );
        redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

    }

    @GetMapping("/finished")
    public int[] finishedTests(HttpServletRequest request) {
        long userId = (long) request.getSession().getAttribute("userId");

        Integer[] finishedTests = codeTestRecordService.
                list(new QueryWrapper<CodeTestRecord>().eq("student_id", userId).isNotNull("end_time"))
                .stream().map(x->Integer.valueOf(x.getCodeType())).toArray(Integer[]::new);

        int[] bitMap = new int[]{0, 0, 0, 0, 0, 0};

        for (int test : finishedTests) {
            bitMap[test - 1] = 1;
        }

        return bitMap;
    }

}

//
//
//@CrossOrigin
//@RestController
//@RequestMapping("CodeTest")
//public class CodeTestController {
//
//    private static final HashMap<String, String> codesTypeId = new HashMap<>();
//    private static final HashMap<String, String[]> limitLibs = new HashMap<>();
//    private static final HashMap<String, String[]> forceContains = new HashMap<>();
//    private static final HashMap<String, String> rightAnswers = new HashMap<>();
//    private static final HashMap<String, String> codePaths = new HashMap<>();
//
//    static {
//        codesTypeId.put("hash", "5");
//        codesTypeId.put("aes", "4");
//        codesTypeId.put("rsa", "3");
//
//        codePaths.put("hash", "CodeTest/hashRunningCodes/");
//        codePaths.put("aes", "CodeTest/aesRunningCodes/");
//        codePaths.put("rsa", "CodeTest/rsaRunningCodes/");
//
//        limitLibs.put("hash", new String[]{"hashlib"});
//        limitLibs.put("aes", new String[]{"Cryptodome.Cipher:AES", "binascii:b2a_hex", "binascii:a2b_hex"});
//        limitLibs.put(
//                "rsa",
//                new String[]{
//                        "Cryptodome.PublicKey:RSA", "Cryptodome.Cipher:PKCS1_v1_5", "Cryptodome.Hash:SHA256",
//                        "Crypto.Signature:PKCS1_v1_5", "base64", "random"
//                }
//        );
//
//        forceContains.put("hash", LoadForceContains.load("CodeTest/FilesForCopy/hash.py"));
//        forceContains.put("aes", LoadForceContains.load("CodeTest/FilesForCopy/aes.py"));
//        forceContains.put("rsa", LoadForceContains.load("CodeTest/FilesForCopy/rsa.py"));
//
//        rightAnswers.put(
//                "aes",
//                "b'be96f99ba8b3331d896c2aa398a41aa3ae81db1a49b2ac6cf92976f97083184e'\n" +
//                        "桂林电子科技大学\n" +
//                        "b'02cbef63143d1cfec26d1019bda98d652ff09b246df9061cddf1a357662dbdc8'\n" +
//                        "桂林电子科技大学\n"
//        );
//        rightAnswers.put(
//                "hash",
//                "29487b3263304dba8c04fbe1169a8b8044e0bf8e\n" +
//                        "51a2141f13aaeec8af96051bc91567b1e2c24a8ff480b87cd963e0ef\n" +
//                        "f0437c34a0c3005abce3733f5c180845b0df755be4515665f2c6dbe0c12184a7\n" +
//                        "cff7f9ed424bd10768c546d6ea63fbd79a90e9fd2e24d895383777d6db92f06243106a34766a4b9ee35e41252ca34736\n" +
//                        "a1937495f4f2bfb4398a4ee4dbf63586631bcbca358416ebd42d63f80bcdac6fa4882f6d81b4480d3b3c7a41625033f3545b8472f02be0626d65fa790f6d549a\n"
//        );
//    }
//
//
//    private final RunCodesRecordService runCodesRecordService;
//
//    @Autowired
//    public CodeTestController(RunCodesRecordService runCodesRecordService) {
//        this.runCodesRecordService = runCodesRecordService;
//    }
//
//    @GetMapping("/getCodes")
//    public String getCodes(HttpServletRequest request, @RequestParam("type") String codesType) {
//
//        long userId = (long) request.getSession().getAttribute("userId");
//        RunCodesRecord runCodesRecord = runCodesRecordService.getOne(
//                new QueryWrapper<RunCodesRecord>()
//                        .eq("student_id", userId)
//                        .eq("code_type", codesTypeId.get(codesType))
//                        .orderByDesc("running_datetime")
//                        .last("limit 1")
//        );
//
//        if (runCodesRecord == null) {
//            return FileOperation.readFile("CodeTest/FilesForCopy/" + codesType + ".py");
//        }
//
//        return runCodesRecord.getCode();
//    }
//
//    @PostMapping("/saveCodes")
//    private void saveCodes(HttpServletRequest request, @RequestParam("type") String codesType) {
//        long userId = (long) request.getSession().getAttribute("userId");
//
//        String codes = FileOperation.getPostData(request);
//
//        runCodesRecordService.save(
//                new RunCodesRecord()
//                        .setStudentId(userId)
//                        .setCodeType(codesTypeId.get(codesType))
//                        .setCode(codes)
//                        .setRunningDatetime(new Date())
//        );
//
//    }
//
//    @PostMapping("/runCodes")
//    private RunCodesResult runCodes(HttpServletRequest request, @RequestParam("type") String codesType)
//            throws IOException {
//
//        long userId = (long) request.getSession().getAttribute("userId");
//        boolean success;
//        String status;
//        String result;
//        RunCodesResult runCodesResult = new RunCodesResult();
//
//        String codes = FileOperation.getPostData(request);
//
//        result = RunPython.runAndGetTraceback(
//                codes, codePaths.get(codesType) + userId + ".py",
//                limitLibs.get(codesType), forceContains.get(codesType)
//        );
//
//        if (codesType.equals("rsa")){
//            String[] answer = Arrays.stream(result.split("\n"))
//                    .filter(x -> x.contains("明文")).toArray(String[]::new);
//
//            success = answer.length == 2 && answer[0].split("：")[1].equals(answer[1].split("：")[1]);
//        } else {
//            success = result.equals(rightAnswers.get(codesType));
//        }
//
//        runCodesResult.setResult(result);
//
//        if (success) {
//            runCodesResult.setStatus("success");
//            status = "success";
//        } else {
//            runCodesResult.setStatus("fail");
//            status = "fail";
//        }
//
//        runCodesRecordService.save(
//                new RunCodesRecord()
//                        .setStudentId(userId)
//                        .setCodeType(codesTypeId.get(codesType))
//                        .setCode(codes)
//                        .setResult(result)
//                        .setStatus(status)
//                        .setRunningDatetime(new Date())
//        );
//
//        return runCodesResult;
//
//    }
//
//}
