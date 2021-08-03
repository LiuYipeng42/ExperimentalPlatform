package com.guet.ExperimentalPlatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("auto_attack_record")
public class AttackRecord {
    public Long userId;
    public int attackTimes;
}
