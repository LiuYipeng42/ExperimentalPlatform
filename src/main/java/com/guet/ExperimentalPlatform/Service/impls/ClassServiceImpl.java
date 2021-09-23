package com.guet.ExperimentalPlatform.Service.impls;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.ExperimentalPlatform.Entity.Class;
import com.guet.ExperimentalPlatform.Service.ClassService;
import com.guet.ExperimentalPlatform.mapper.ClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class>
        implements ClassService {

    ClassMapper classMapper;

    @Autowired
    public void setClassMapper(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public void addClassStudent(long studentId, long classId) {
        classMapper.addClassStudent(studentId, classId);
    }

    @Override
    public void removeClassStudent(long studentId) {
        classMapper.removeClassStudent(studentId);
    }
}
