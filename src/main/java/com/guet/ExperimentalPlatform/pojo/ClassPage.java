package com.guet.ExperimentalPlatform.pojo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ClassPage<T> extends Page<T> {
    private static final long serialVersionUID = 5194933845448697148L;

    private String classNum;

    public ClassPage(long current, long size) {
        super(current, size, false);
    }
}
