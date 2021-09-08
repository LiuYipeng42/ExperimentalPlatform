package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.AlgorithmRecord;
import com.guet.ExperimentalPlatform.mapper.AlgorithmRecordMapper;
import com.guet.ExperimentalPlatform.Service.AlgorithmRecordService;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmRecordServiceImpl extends ServiceImpl<AlgorithmRecordMapper, AlgorithmRecord>
        implements AlgorithmRecordService {
}
