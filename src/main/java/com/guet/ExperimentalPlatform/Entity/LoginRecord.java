package com.guet.ExperimentalPlatform.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("login_record")
public class LoginRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private long StudentId;

    private Date loginTime;
}
