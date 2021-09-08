package com.guet.ExperimentalPlatform.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("algorithm_record")
public class AlgorithmRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private long studentId;

    private String algorithm;

}
