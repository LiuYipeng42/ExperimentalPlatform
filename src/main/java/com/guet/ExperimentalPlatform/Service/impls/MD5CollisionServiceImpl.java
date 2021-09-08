package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.Entity.MD5TaskRecord;
import com.guet.ExperimentalPlatform.mapper.MD5TaskRecordMapper;
import com.guet.ExperimentalPlatform.Service.MD5CollisionService;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class MD5CollisionServiceImpl extends ServiceImpl<MD5TaskRecordMapper, MD5TaskRecord>
        implements MD5CollisionService {

    @Override
    public void createEnvironment(String userId) throws IOException {
        RunCMD.execute(
                "cp -r " +
                        "MD5CollisionFiles/OriginalFiles " +
                        "MD5CollisionFiles/ExperimentDataFile/" + userId);
    }

    @Override
    public void closeEnvironment(String userId) throws IOException {
        RunCMD.execute(
                "rm -rf " +
                        "MD5CollisionFiles/ExperimentDataFile/" + userId);
    }
}
