package com.guet.ExperimentalPlatform.controller;

import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import com.guet.ExperimentalPlatform.entity.RunCodesRecord;
import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import com.guet.ExperimentalPlatform.service.RunCodesRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;


@CrossOrigin
@RestController
@RequestMapping("PaddingOracle")
public class PaddingOracleController {

    private final PaddingOracleService paddingOracleService;
    private final RunCodesRecordService runCodesRecordService;

    @Autowired
    public PaddingOracleController(PaddingOracleService paddingOracleService,
                                   RunCodesRecordService runCodesRecordService) {
        this.paddingOracleService = paddingOracleService;
        this.runCodesRecordService = runCodesRecordService;
    }

    @GetMapping("/createEnvironment")
    public boolean createPaddingOracleEnvironment(HttpServletRequest request) {

        long userId = (long) request.getSession().getAttribute("userId");

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

    @PostMapping("/auto_attack")
    public String runAutoAttack(HttpServletRequest request) throws IOException {

        long userId = (long) request.getSession().getAttribute("userId");
        RunCodesRecord runCodesRecord;
        String result;
        String status;

        runCodesRecord = RunPython.runPostCodes(
                request, "PaddingOracleFiles/ExperimentDataFile/" + userId + "_auto_attack.py",
                "2", new String[]{"socket", "binascii:hexlify", "binascii:unhexlify"}
        );

        result = runCodesRecord.getResult();

        if (result.contains("Congraduations! you've got the plain!")) {
            result += "\n已获取正确密文!";
            status = "success";
        } else {
            result += "\n获取正确密文失败!";
            status = "fail";
        }

        runCodesRecordService.save(runCodesRecord.setStudentId(userId).setStatus(status));

        return result;

    }

    @PostMapping("/manual_attack")
    public String runManualAttack(HttpServletRequest request) throws IOException {

        long userId = (long) request.getSession().getAttribute("userId");

        RunCodesRecord result = RunPython.runPostCodes(
                request, "PaddingOracleFiles/ExperimentDataFile/" + userId + "_manual_attack.py",
                "1", 0.84,
                new String[]{"socket", "binascii:hexlify", "binascii:unhexlify"}
        );

        runCodesRecordService.save(result.setStudentId(userId));

        return result.getResult();

    }

}
