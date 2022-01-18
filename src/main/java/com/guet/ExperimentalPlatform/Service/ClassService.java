package com.guet.ExperimentalPlatform.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.Entity.Class;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassService extends IService<Class> {

    void addClassStudent(long studentId, long classId);

    List<Long> selectTeacherIdByClassNum(String classNum);
}
