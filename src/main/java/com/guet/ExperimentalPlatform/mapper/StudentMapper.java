package com.guet.ExperimentalPlatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.ExperimentalPlatform.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
