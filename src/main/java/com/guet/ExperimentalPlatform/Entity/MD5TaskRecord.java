package com.guet.ExperimentalPlatform.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("MD5_task_record")
public class MD5TaskRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private long studentId;

    private String taskName;

    private Date startTime;

    private Date endTime;

    private String status;
}
