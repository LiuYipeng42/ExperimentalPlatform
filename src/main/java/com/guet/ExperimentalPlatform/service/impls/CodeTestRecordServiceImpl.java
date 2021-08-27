package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.CodeTestRecord;
import com.guet.ExperimentalPlatform.mapper.CodeTestRecordMapper;
import com.guet.ExperimentalPlatform.service.CodeTestRecordService;
import org.springframework.stereotype.Service;

@Service
public class CodeTestRecordServiceImpl extends ServiceImpl<CodeTestRecordMapper, CodeTestRecord>
        implements CodeTestRecordService {
}
