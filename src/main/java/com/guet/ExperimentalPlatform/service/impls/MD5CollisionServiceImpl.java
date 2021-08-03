package com.guet.ExperimentalPlatform.service.impls;

import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.service.MD5CollisionService;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class MD5CollisionServiceImpl implements MD5CollisionService {

    @Override
    public void createEnvironment(String userId) throws IOException {
        RunCMD.runCMD(
                "cp -r " +
                        "MD5CollisionFiles/OriginalFiles " +
                        "MD5CollisionFiles/ExperimentDataFile/" + userId);
    }

    @Override
    public void closeEnvironment(String userId) throws IOException {
        RunCMD.runCMD(
                "rm -rf " + "MD5CollisionFiles/ExperimentDataFile/" + userId);
    }
}
