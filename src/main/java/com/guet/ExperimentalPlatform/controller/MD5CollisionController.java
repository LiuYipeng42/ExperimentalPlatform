package com.guet.ExperimentalPlatform.controller;


import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.service.MD5CollisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("MD5Collision")
public class MD5CollisionController {

    MD5CollisionService md5CollisionService;

    @Autowired
    public MD5CollisionController(MD5CollisionService md5CollisionService) {
        this.md5CollisionService = md5CollisionService;
    }

    @GetMapping("/createEnvironment/{userAccount}")
    public void createMD5CollisionEnvironment(HttpServletRequest request,
                                              @PathVariable("userAccount") String userAccount) throws IOException {

        md5CollisionService.createEnvironment(
                (String) request.getSession().getAttribute(userAccount)
        );
    }

    @GetMapping("/closeEnvironment/{userAccount}")
    public void closeMD5CollisionEnvironment(HttpServletRequest request,
                                             @PathVariable("userAccount") String userAccount) throws IOException {

        md5CollisionService.closeEnvironment(
                (String) request.getSession().getAttribute(userAccount)
        );
    }

    @GetMapping("/{command}/{userAccount}")
    public String runCommands(HttpServletRequest request,
                              @PathVariable("userAccount") String userAccount,
                              @PathVariable("command") String command) throws IOException {

        String userId = (String) request.getSession().getAttribute(userAccount);
        String filePath = "MD5CollisionFiles/ExperimentDataFile/" + userId;

        switch (command) {
            case "run":
                return RunCMD.runCMD("./" + filePath + "/");
            case "hex":
                return RunCMD.runCMD("hexdump -Cv " + filePath + "/");
            case "make":
                return RunCMD.runCMD("make -C " + filePath);
            case "makeClean":
                return RunCMD.runCMD("make clean -C " + filePath);
        }
        return command + ": 未找到命令";
    }

    @GetMapping("/getFile/{fileName}/{userAccount}")
    public String getFile(HttpServletRequest request,
                                @PathVariable("userAccount") String userAccount,
                                @PathVariable("fileName") String fileName) {

        return FileOperation.readFile(
                "MD5CollisionFiles/ExperimentDataFile/" +
                        request.getSession().getAttribute(userAccount)
                        + "/" + fileName
        );

    }

    @PostMapping("/saveFile/{fileName}/{userAccount}")
    public void saveFile(HttpServletRequest request,
                               @PathVariable("userAccount") String userAccount,
                               @PathVariable("fileName") String fileName) {

        FileOperation.savePostText(
                request,
                "MD5CollisionFiles/ExperimentDataFile/" +
                        request.getSession().getAttribute(userAccount)
                        + "/" + fileName
        );

    }

}
