package com.guet.ExperimentalPlatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunAttack;
import com.guet.ExperimentalPlatform.entity.POAutoAttackRecord;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.service.POAutoAttackRecordService;
import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
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
    private final StudyRecordService studyRecordService;
    private final POAutoAttackRecordService poAutoAttackRecordService;

    @Autowired
    public PaddingOracleController(PaddingOracleService paddingOracleService, StudyRecordService studyRecordService,
                                   POAutoAttackRecordService poAutoAttackRecordService) {
        this.paddingOracleService = paddingOracleService;
        this.studyRecordService = studyRecordService;
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

        if (
                poAutoAttackRecordService.getOne(
                        new QueryWrapper<POAutoAttackRecord>().select("student_id", String.valueOf(userId))
                ) == null
        ) {
                poAutoAttackRecordService.save(
                        new POAutoAttackRecord()
                                .setStudentId(userId)
                                .setAutoAttackTimes(0)
                );
        }

        studyRecordService.save(
                new StudyRecord()
                        .setStudentId(userId)
                        .setStartTime(new Date())
                        .setExperimentType(2)
        );

        return true;

    }

    @GetMapping("/closeEnvironment")
    public boolean closePaddingOracleEnvironment(HttpServletRequest request) {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        try {
            paddingOracleService.closeEnvironment(
                    String.valueOf(userId)
            );

            studyRecordService.update(
                    null,
                    new UpdateWrapper<StudyRecord>().set("end_time", new Date())
                            .eq("student_id", userId)
                            .eq("experiment_type", 2)
                            .isNull("end_time")
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

        String result = RunAttack.runAttack(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        userId +
                        "_auto_attack.py"
        );

        if (result.contains("Congraduations! you've got the plain!")) {
            result += "\n已获取正确密文!";
        } else {
            result += "\n获取正确密文失败!";

            poAutoAttackRecordService.addOne(userId);

        }

        return result;

    }

    @GetMapping("/manual_attack")
    public String runManualAttack(HttpServletRequest request) throws IOException {

        long userId = Long.parseLong((String) request.getSession().getAttribute("userId"));

        return RunAttack.runAttack(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        userId +
                        "_manual_attack.py"
        );

    }

}
