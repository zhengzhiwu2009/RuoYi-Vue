package com.ruoyi.ailearn.course.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.course.domain.Subject;
import com.ruoyi.ailearn.course.mapper.SubjectMapper;
import com.ruoyi.ailearn.course.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学科Service实现类
 *
 * @author: LiuYang
 * @create: 2025-12-04 14:25
 **/
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Override
    public List<Subject> getAllSubjects() {
        return this.list();
    }
}

