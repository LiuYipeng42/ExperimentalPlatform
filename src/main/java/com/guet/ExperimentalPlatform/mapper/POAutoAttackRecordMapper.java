package com.guet.ExperimentalPlatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.ExperimentalPlatform.entity.POAutoAttackRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface POAutoAttackRecordMapper extends BaseMapper<POAutoAttackRecord> {

    @Update("update PO_auto_attack_record set auto_attack_times = auto_attack_times + 1 " +
            "where student_id = ${userId}")
    boolean addOne(long userId);
}
