package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.StudyRecord;
import com.guet.ExperimentalPlatform.mapper.StudyRecordMapper;
import com.guet.ExperimentalPlatform.service.StudyRecordService;
import org.springframework.stereotype.Service;

@Service
public class StudyRecordServiceImpl  extends ServiceImpl<StudyRecordMapper, StudyRecord>
        implements StudyRecordService {
}
