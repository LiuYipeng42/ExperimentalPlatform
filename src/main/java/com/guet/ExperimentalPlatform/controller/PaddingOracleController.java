package com.guet.ExperimentalPlatform.controller;

import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunPython;
import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@CrossOrigin
@RestController
@RequestMapping("PaddingOracle")
public class PaddingOracleController {

    PaddingOracleService paddingOracleService;

    @Autowired
    public PaddingOracleController(PaddingOracleService paddingOracleService) {
        this.paddingOracleService = paddingOracleService;
    }

    @GetMapping("/createEnvironment/{userAccount}")
    public boolean createPaddingOracleEnvironment(HttpServletRequest request,
                                                  @PathVariable("userAccount") String userAccount) {

        try {
            paddingOracleService.createEnvironment(
                    (String) request.getSession().getAttribute(userAccount),
                    "handsonsecurity/seed-server:padding-oracle"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    @GetMapping("/closeEnvironment/{userAccount}")
    public boolean closePaddingOracleEnvironment(HttpServletRequest request,
                                                 @PathVariable("userAccount") String userAccount) {

        try {
            paddingOracleService.closeEnvironment(
                    (String) request.getSession().getAttribute(userAccount)
            );
            return true;
        } catch (NullPointerException e) {
            return false;
        }

    }

    @GetMapping("/getFile/{fileName}/{userAccount}")
    public String getPythonFile(HttpServletRequest request,
                                @PathVariable("userAccount") String userAccount,
                                @PathVariable("fileName") String fileName) {

        return FileOperation.readFile(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        request.getSession().getAttribute(userAccount) +
                        "_" +
                        fileName +
                        ".py"
        );

    }

    @PostMapping("/saveFile/{fileName}/{userAccount}")
    public void savePythonFile(HttpServletRequest request,
                               @PathVariable("userAccount") String userAccount,
                               @PathVariable("fileName") String fileName) {

        FileOperation.savePostText(
                request,
                "PaddingOracleFiles/ExperimentDataFile/" +
                        request.getSession().getAttribute(userAccount) +
                        "_" +
                        fileName +
                        ".py"
        );

    }

    @GetMapping("/auto_attack/{userAccount}")
    public String runAutoAttack(HttpServletRequest request,
                                @PathVariable("userAccount") String userAccount) throws IOException {

        return RunPython.runPython(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        request.getSession().getAttribute(userAccount) +
                        "_auto_attack.py "
        );

    }

    @GetMapping("/manual_attack/{userAccount}")
    public String runManualAttack(HttpServletRequest request,
                                  @PathVariable("userAccount") String userAccount) throws IOException {

        return RunPython.runPython(
                "PaddingOracleFiles/ExperimentDataFile/" +
                        request.getSession().getAttribute(userAccount) +
                        "_manual_attack.py"
        );

    }

}
