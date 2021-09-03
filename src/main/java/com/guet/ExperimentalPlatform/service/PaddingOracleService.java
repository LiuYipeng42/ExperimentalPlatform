package com.guet.ExperimentalPlatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.entity.PORunCodesRecord;

import java.io.IOException;


public interface PaddingOracleService extends IService<PORunCodesRecord> {

    boolean createEnvironment(String containerName, String imageName) throws IOException;

    void closeEnvironment(String userId);

    void copyCodes(String userId);

}
