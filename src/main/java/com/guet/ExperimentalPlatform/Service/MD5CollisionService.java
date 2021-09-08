package com.guet.ExperimentalPlatform.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.Entity.MD5TaskRecord;

import java.io.IOException;

public interface MD5CollisionService extends IService<MD5TaskRecord> {
    void createEnvironment(String userId) throws IOException;

    void closeEnvironment(String userId) throws IOException;
}
