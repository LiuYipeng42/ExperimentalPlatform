package com.guet.ExperimentalPlatform.service;

import java.io.IOException;

public interface MD5CollisionService {
    void createEnvironment(String userId) throws IOException;

    void closeEnvironment(String userId) throws IOException;
}
