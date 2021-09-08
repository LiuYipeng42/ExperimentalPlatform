package com.guet.ExperimentalPlatform.Controller;

import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.LoadForceContains;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import com.guet.ExperimentalPlatform.Entity.PORunCodesRecord;
import com.guet.ExperimentalPlatform.Service.PaddingOracleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("PaddingOracle")
public class PaddingOracleController {

    private final PaddingOracleService paddingOracleService;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String originalFile =
            Arrays.stream(FileOperation.readFile("PaddingOracleFiles/OriginalFiles/manual_attack.py").split("\n"))
                    .filter(x -> !x.contains("#"))
                    .collect(Collectors.joining()).replace(" ", "");

    private static final HashMap<String, String[]> forceContains = new HashMap<>();

    static {
        forceContains.put("manual_attack", LoadForceContains.load("PaddingOracleFiles/OriginalFiles/manual_attack.py"));
        forceContains.put("auto_attack", LoadForceContains.load("PaddingOracleFiles/OriginalFiles/auto_attack.py"));
    }

    @Autowired
    public PaddingOracleController(PaddingOracleService paddingOracleService, RedisTemplate<String, Object> redisTemplate) {
        this.paddingOracleService = paddingOracleService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/createEnvironment")
    public boolean createPaddingOracleEnvironment(HttpServletRequest request) {

        long userId = (long) request.getSession().getAttribute("userId");

        try {
            paddingOracleService.createEnvironment(
                    String.valueOf(userId),
                    "guet/security-server:padding-oracle"
            );
        } catch (IOException e) {
            return false;
        }
        return true;

    }

    @GetMapping("/closeEnvironment")
    public boolean closePaddingOracleEnvironment(HttpServletRequest request) {

        long userId = (long) request.getSession().getAttribute("userId");

        try {
            paddingOracleService.closeEnvironment(
                    String.valueOf(userId)
            );

            return true;
        } catch (NullPointerException e) {
            return false;
        }

    }

    @GetMapping("/getFile/{fileName}")
    public String getPythonFile(HttpServletRequest request,
                                @PathVariable("fileName") String fileName) {

        long userId = (long) request.getSession().getAttribute("userId");

        return FileOperation.readFile(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        userId +
                        "_" +
                        fileName +
                        ".py"
        );

    }

    @GetMapping("/reset/{fileName}")
    public String resetCodes(HttpServletRequest request,
                                @PathVariable("fileName") String fileName) {

        long userId = (long) request.getSession().getAttribute("userId");

        paddingOracleService.copyCodes(String.valueOf(userId));

        return FileOperation.readFile(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        userId +
                        "_" +
                        fileName +
                        ".py"
        );
    }

    @PostMapping("/auto_attack")
    public String runAutoAttack(HttpServletRequest request) throws IOException {

        long userId = (long) request.getSession().getAttribute("userId");
        String result;
        String status;

        String codes = FileOperation.savePostText(
                request, "PaddingOracleFiles/ExperimentDataFile/" + userId + "_auto_attack.py"
        );

        result = RunPython.run(
                codes, "PaddingOracleFiles/ExperimentDataFile/tempCodes/" + userId + "_auto_attack.py",
                forceContains.get("auto_attack"), new String[]{"socket", "binascii:hexlify", "binascii:unhexlify"}
        );

        if (result.contains("Congraduations! you've got the plain!")) {
            result += "\n已获取正确密文!";
            status = "success";
            redisTemplate.opsForValue().setBit("reportUpdate", userId, true);
        } else {
            result += "\n获取正确密文失败!";
            status = "fail";
        }

        paddingOracleService.save(
                new PORunCodesRecord()
                        .setStudentId(userId)
                        .setCodeType("auto_attack")
                        .setStatus(status)
                        .setRunningDatetime(new Date())
        );

        return result;

    }

    @PostMapping("/manual_attack")
    public String runManualAttack(HttpServletRequest request) throws IOException {

        long userId = (long) request.getSession().getAttribute("userId");

        String codes = FileOperation.savePostText(
                request, "PaddingOracleFiles/ExperimentDataFile/" + userId + "_manual_attack.py"
        );

        String result = RunPython.run(
                codes, "PaddingOracleFiles/ExperimentDataFile/tempCodes/" + userId + "_manual_attack.py",
                originalFile, 0.84, forceContains.get("manual_attack"),
                new String[]{"socket", "binascii:hexlify", "binascii:unhexlify"}
        );

        paddingOracleService.save(
                new PORunCodesRecord()
                        .setStudentId(userId)
                        .setCodeType("manual_attack")
                        .setRunningDatetime(new Date())
        );

        redisTemplate.opsForValue().setBit("reportUpdate", userId, true);

        return result;

    }

}
