package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.StudyRecord;
import com.guet.ExperimentalPlatform.mapper.StudyRecordMapper;
import com.guet.ExperimentalPlatform.Service.StudyRecordService;
import org.springframework.stereotype.Service;

@Service
public class StudyRecordServiceImpl extends ServiceImpl<StudyRecordMapper, StudyRecord>
        implements StudyRecordService {
}
