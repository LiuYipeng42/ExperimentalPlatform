package com.guet.ExperimentalPlatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.ExperimentalPlatform.Entity.Class;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ClassMapper extends BaseMapper<Class> {

    @Select("insert into student_class (student_id, class_id) values (#{studentId}, #{classId})")
    void addClassStudent(@Param("studentId") long studentId, @Param("classId") long classId);

    @Delete("delete from student_class where student_id=#{studentId}")
    void removeClassStudent(@Param("studentId") long studentId);

}
