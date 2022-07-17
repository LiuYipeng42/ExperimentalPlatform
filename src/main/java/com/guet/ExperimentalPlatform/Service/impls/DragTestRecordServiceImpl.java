package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.DragTestRecord;
import com.guet.ExperimentalPlatform.Service.DragTestRecordService;
import com.guet.ExperimentalPlatform.mapper.DragTestRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class DragTestRecordServiceImpl extends ServiceImpl<DragTestRecordMapper, DragTestRecord>
        implements DragTestRecordService {
}
