package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.AlgorithmRecord;
import com.guet.ExperimentalPlatform.entity.CodeTestRecord;
import com.guet.ExperimentalPlatform.mapper.AlgorithmRecordMapper;
import com.guet.ExperimentalPlatform.mapper.CodeTestRecordMapper;
import com.guet.ExperimentalPlatform.service.AlgorithmRecordService;
import com.guet.ExperimentalPlatform.service.CodeTestRecordService;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmRecordServiceImpl extends ServiceImpl<AlgorithmRecordMapper, AlgorithmRecord>
        implements AlgorithmRecordService {
}
