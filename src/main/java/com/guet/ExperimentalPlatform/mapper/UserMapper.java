package com.guet.ExperimentalPlatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.pojo.ClassPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select(
            "select * from `user` where id in " +
                    "(select student_id from student_class where class_id in " +
                    "(select id from class where class_num=#{classNum})" +
                    ")"
    )
    List<User> selectUserByClassNum(@Param("classNum") String classNum);

    @Select(
            "select * from `user` where id in " +
                    "(select student_id from student_class where class_id in " +
                    "(select id from class where teacher_id=#{teacherId})" +
                    ")"
    )
    List<User> selectUserByTeacher(@Param("teacherId") String teacherId);

    @Select("select * from user where identity!='teacher'")
    List<User> getAllStudents();

    @Select(
            "select count(*) from `user` where id in " +
                    "(select student_id from student_class where class_id in " +
                    "(select id from class where class_num=#{classNum})" +
                    ")"
    )
    Integer countStudentsByClassNum(@Param("classNum") String classNum);


}
