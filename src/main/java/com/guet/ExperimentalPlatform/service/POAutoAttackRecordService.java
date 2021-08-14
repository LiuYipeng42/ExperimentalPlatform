package com.guet.ExperimentalPlatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.entity.POAutoAttackRecord;


public interface POAutoAttackRecordService extends IService<POAutoAttackRecord> {
    boolean addOne(long userId);
}
