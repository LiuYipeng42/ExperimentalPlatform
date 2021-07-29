package com.guet.ExperimentalPlatform.service.impls;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;

import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.pojo.ContainerInfo;
import com.guet.ExperimentalPlatform.service.PaddingOracleService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class PaddingOracleServiceImpl implements PaddingOracleService {

    private final DockerClient client;
    private static final ConcurrentHashMap<String, ContainerInfo> userIdContainer = new ConcurrentHashMap<>();

    public PaddingOracleServiceImpl(){
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://0.0.0.0:2375").build();
        client = DockerClientBuilder.getInstance(config).build();
    }

    public boolean createEnvironment(String userId, String imageName) {
        CreateContainerResponse container;
        // 创建容器

        container = client.createContainerCmd(imageName)
                .withName(userId).exec();

        String containerId = container.getId();

        // 启动容器
        client.startContainerCmd(containerId).exec();

        String containerIP = client.inspectContainerCmd(container.getId()).exec()
                .getNetworkSettings()
                .getNetworks()
                .get("bridge")
                .getIpAddress();

        System.out.println(containerIP);

        userIdContainer.put(userId,
                new ContainerInfo()
                        .setContainerId(containerId)
                        .setContainerIP(containerIP)
        );

        // 复制文件
        FileOperation.copyAndReplace("PaddingOracleFiles/OriginalFiles/manual_attack.py",
                "PaddingOracleFiles/ExperimentDataFile/" + userId + "_manual_attack.py",
                "\"containerIP\"", "\"" + containerIP +"\"");

        FileOperation.copyAndReplace("PaddingOracleFiles/OriginalFiles/auto_attack.py",
                "PaddingOracleFiles/ExperimentDataFile/" + userId + "_auto_attack.py",
                "\"containerIP\"", "\"" + containerIP +"\"");

        return true;
    }

    public void closeEnvironment(String userId){
        ContainerInfo containerInfo = userIdContainer.get(userId);

        String containerId = containerInfo.getContainerId();
        // 关闭容器
        client.stopContainerCmd(containerId).exec();
        // 删除容器
        client.removeContainerCmd(containerId).exec();
        // 删除文件
        userIdContainer.remove(userId);
        new File("PaddingOracleFiles/ExperimentDataFile/" + userId + "_manual_attack.py").delete();
        new File("PaddingOracleFiles/ExperimentDataFile/" + userId + "_auto_attack.py").delete();

    }

}
