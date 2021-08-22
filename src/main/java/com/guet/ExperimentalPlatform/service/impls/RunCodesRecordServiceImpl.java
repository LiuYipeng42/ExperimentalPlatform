package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.RunCodesRecord;
import com.guet.ExperimentalPlatform.mapper.RunCodesRecordMapper;
import com.guet.ExperimentalPlatform.service.RunCodesRecordService;
import org.springframework.stereotype.Service;

@Service
public class RunCodesRecordServiceImpl extends ServiceImpl<RunCodesRecordMapper, RunCodesRecord>
        implements RunCodesRecordService {
}
