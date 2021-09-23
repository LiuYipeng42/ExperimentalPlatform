package com.guet.ExperimentalPlatform.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.ExperimentalPlatform.Entity.Class;

public interface ClassService extends IService<Class> {

    void addClassStudent(long studentId, long classId);

    void removeClassStudent(long studentId);
}
