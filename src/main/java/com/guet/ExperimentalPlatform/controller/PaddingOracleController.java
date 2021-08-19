package com.guet.ExperimentalPlatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import com.guet.ExperimentalPlatform.entity.POAutoAttackRecord;
import com.guet.ExperimentalPlatform.service.POAutoAttackRecordService;
import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@CrossOrigin
@RestController
@RequestMapping("PaddingOracle")
public class PaddingOracleController {

    private final PaddingOracleService paddingOracleService;
    private final POAutoAttackRecordService poAutoAttackRecordService;

    @Autowired
    public PaddingOracleController(PaddingOracleService paddingOracleService,
                                   POAutoAttackRecordService poAutoAttackRecordService) {
        this.paddingOracleService = paddingOracleService;
        this.poAutoAttackRecordService = poAutoAttackRecordService;
    }

    @GetMapping("/createEnvironment")
    public boolean createPaddingOracleEnvironment(HttpServletRequest request) {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        try {
            paddingOracleService.createEnvironment(
                    String.valueOf(userId),
                    "guet/security-server:padding-oracle"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    @GetMapping("/closeEnvironment")
    public boolean closePaddingOracleEnvironment(HttpServletRequest request) {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

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

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        return FileOperation.readFile(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        userId +
                        "_" +
                        fileName +
                        ".py"
        );

    }

    @PostMapping("/saveFile/{fileName}")
    public void savePythonFile(HttpServletRequest request,
                               @PathVariable("fileName") String fileName) {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        FileOperation.savePostText(
                request,
                "PaddingOracleFiles/ExperimentDataFile/" +
                        userId +
                        "_" +
                        fileName +
                        ".py"
        );

    }

    @GetMapping("/auto_attack")
    public String runAutoAttack(HttpServletRequest request) throws IOException {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        String result = RunPython.run(
                "PaddingOracleFiles/ExperimentDataFile/" + userId + "_auto_attack.py",
                new String[]{"socket", "hexlify", "unhexlify"}
        );

        if (result.contains("Congraduations! you've got the plain!")) {
            result += "\n已获取正确密文!";
        } else {
            result += "\n获取正确密文失败!";

//            poAutoAttackRecordService.save();

        }

        return result;

    }

    @GetMapping("/manual_attack")
    public String runManualAttack(HttpServletRequest request) throws IOException {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        return RunPython.run(
                "PaddingOracleFiles/ExperimentDataFile/" + userId + "_manual_attack.py",
                "manualAttack",
                0.84,
                new String[]{"socket", "hexlify", "unhexlify"}
        );

    }

}
