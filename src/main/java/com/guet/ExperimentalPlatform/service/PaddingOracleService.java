package com.guet.ExperimentalPlatform.service;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public interface PaddingOracleService {

    boolean createEnvironment(String containerName, String imageName) throws IOException;

    void closeEnvironment(String userId);

}
