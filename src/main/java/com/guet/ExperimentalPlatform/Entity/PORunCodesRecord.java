package com.guet.ExperimentalPlatform.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("PO_run_codes_record")
public class PORunCodesRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long studentId;

    private String codeType;

    private String status;

    private Date runningDatetime;
}
