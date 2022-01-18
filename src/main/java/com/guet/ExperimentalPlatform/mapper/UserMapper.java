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

    @Select(
            "select teacher_id from class where id in (" +
                    "select class_id from student_class where student_id in " +
                    "(select id from user where account=#{account})" +
                    ")"
    )
    List<Long> selectTeacherIdByAccount(@Param("account") String account);

    @Select(
            "select teacher_id from class where id in (" +
                    "select class_id from student_class where student_id = #{id})"
    )
    List<Long> selectTeacherIdById(@Param("id") long id);

    @Select("select * from user where identity!='teacher'")
    List<User> getAllStudents();

    @Select(
            "select count(*) from `user` where id in " +
                    "(select student_id from student_class where class_id in " +
                    "(select id from class where class_num=#{classNum})" +
                    ")"
    )
    Integer countStudentsByClassNum(@Param("classNum") String classNum);

    @Select(
            "SELECT class_num FROM class WHERE id in (SELECT class_id FROM `student_class` WHERE student_id = #{id});"
    )
    List<String> getClassNum(@Param("id") long id);
}
