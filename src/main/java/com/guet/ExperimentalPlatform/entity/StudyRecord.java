package com.guet.ExperimentalPlatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("study_record")
public class StudyRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private long StudentId;

    private Date loginTime;

    private Date logoutTime;
}
