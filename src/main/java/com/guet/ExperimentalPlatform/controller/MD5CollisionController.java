package com.guet.ExperimentalPlatform.controller;


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
    public MD5CollisionController(MD5CollisionService md5CollisionService){
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

}
