package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.CognitionTestRecord;
import com.guet.ExperimentalPlatform.Entity.DragTestRecord;
import com.guet.ExperimentalPlatform.Service.CognitionTestRecordService;
import com.guet.ExperimentalPlatform.Service.DragTestRecordService;
import com.guet.ExperimentalPlatform.mapper.CognitionTestRecordMapper;
import com.guet.ExperimentalPlatform.mapper.DragTestRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class CognitionTestRecordServiceImpl extends ServiceImpl<CognitionTestRecordMapper, CognitionTestRecord>
        implements CognitionTestRecordService {
}
