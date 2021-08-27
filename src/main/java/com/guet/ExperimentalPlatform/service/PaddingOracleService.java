package com.guet.ExperimentalPlatform.service;

import java.io.IOException;


public interface PaddingOracleService {

    boolean createEnvironment(String containerName, String imageName) throws IOException;

    void closeEnvironment(String userId);

    void copyCodes(String userId);

}
