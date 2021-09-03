package com.guet.ExperimentalPlatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("student")
public class Student{

    @TableId(type = IdType.AUTO)
    private Long id;

    private String account;

    private String password;

    private String name;

    private String classId;

    private String summary;

}
