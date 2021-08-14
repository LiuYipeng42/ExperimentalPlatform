package com.guet.ExperimentalPlatform.service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.entity.POAutoAttackRecord;
import com.guet.ExperimentalPlatform.mapper.POAutoAttackRecordMapper;
import com.guet.ExperimentalPlatform.service.POAutoAttackRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class POAutoAttackRecordServiceImpl extends ServiceImpl<POAutoAttackRecordMapper, POAutoAttackRecord>
        implements POAutoAttackRecordService {

    POAutoAttackRecordMapper poAutoAttackRecordMapper;

    @Autowired
    public void setPoAutoAttackRecordMapper(POAutoAttackRecordMapper poAutoAttackRecordMapper) {
        this.poAutoAttackRecordMapper = poAutoAttackRecordMapper;
    }

    @Override
    public boolean addOne(long userId) {
        return poAutoAttackRecordMapper.addOne(userId);
    }
}
