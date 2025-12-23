package com.ruoyi.ailearn.course.service;

import com.ruoyi.ailearn.course.domain.Subject;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 14:25
 **/
public interface SubjectService {

    /**
     * 获取课程列表
     * @return
     */
    List<Subject> getAllSubjects();
}
