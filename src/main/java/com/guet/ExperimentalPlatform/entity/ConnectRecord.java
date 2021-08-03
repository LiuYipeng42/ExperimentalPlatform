package com.guet.ExperimentalPlatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("connect_record")
public class ConnectRecord{

    @TableId(type = IdType.AUTO)
    Long id;

    long senderId;

    long receiverId;

    Date connectTime;

    String status;

    public ConnectRecord(long sender, long receiver, Date connectTime) {
        this.senderId = sender;
        this.receiverId = receiver;
        this.connectTime = connectTime;
    }

}
