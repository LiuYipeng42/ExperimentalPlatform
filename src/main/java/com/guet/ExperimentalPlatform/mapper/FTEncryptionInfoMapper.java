package com.guet.ExperimentalPlatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.ExperimentalPlatform.Entity.FTAllInfo;
import com.guet.ExperimentalPlatform.Entity.FTEncryptionInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FTEncryptionInfoMapper extends BaseMapper<FTEncryptionInformation> {

    @Select("SELECT * FROM FT_encryption_information, FT_connect_record where " +
            "connect_id = FT_connect_record.id and " +
            "(sender_id = ${userId} or receiver_id = ${userId}) and " +
            "`status` = 'accepted' and " +
            "digital_sign is NOT NULL;")
    List<FTAllInfo> getAllInfo(@Param("userId") long userId);
}
