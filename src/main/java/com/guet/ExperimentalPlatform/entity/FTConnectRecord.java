package com.guet.ExperimentalPlatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("FT_connect_record")
public class FTConnectRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long senderId;

    private long receiverId;

    private Date connectTime;

    private String status;

    public FTConnectRecord(long sender, long receiver, Date connectTime) {
        this.senderId = sender;
        this.receiverId = receiver;
        this.connectTime = connectTime;
    }

}
