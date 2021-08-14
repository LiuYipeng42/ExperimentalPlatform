package com.guet.ExperimentalPlatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("PO_auto_attack_record")
public class POAutoAttackRecord {

    @TableId(type = IdType.AUTO)
    private long id;

    private long studentId;

    private int autoAttackTimes;
}
