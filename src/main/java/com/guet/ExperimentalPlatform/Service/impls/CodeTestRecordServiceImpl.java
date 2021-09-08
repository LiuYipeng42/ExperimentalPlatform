package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.CodeTestRecord;
import com.guet.ExperimentalPlatform.mapper.CodeTestRecordMapper;
import com.guet.ExperimentalPlatform.Service.CodeTestRecordService;
import org.springframework.stereotype.Service;

@Service
public class CodeTestRecordServiceImpl extends ServiceImpl<CodeTestRecordMapper, CodeTestRecord>
        implements CodeTestRecordService {
}
