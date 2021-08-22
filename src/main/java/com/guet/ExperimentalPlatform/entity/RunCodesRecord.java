package com.guet.ExperimentalPlatform.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("run_codes_record")
public class RunCodesRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long studentId;

    private String codeType;

    private String code;

    private String result;

    private String status;

    private Date runningDatetime;
}
