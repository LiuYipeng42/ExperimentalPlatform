package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.PORunCodesRecord;
import com.guet.ExperimentalPlatform.mapper.PORunCodesRecordMapper;
import com.guet.ExperimentalPlatform.service.PORunCodesRecordService;
import org.springframework.stereotype.Service;

@Service
public class PORunCodesRecordServiceImpl extends ServiceImpl<PORunCodesRecordMapper, PORunCodesRecord>
        implements PORunCodesRecordService {
}
