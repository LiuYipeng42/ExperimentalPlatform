package com.guet.ExperimentalPlatform.pojo;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ContainerInfo {
    String containerId;
    String containerIP;
}
