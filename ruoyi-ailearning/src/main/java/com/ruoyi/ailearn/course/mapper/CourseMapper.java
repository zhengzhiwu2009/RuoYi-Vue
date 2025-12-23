package com.ruoyi.ailearn.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.course.domain.Course;
import com.ruoyi.system.domain.SysOperLog;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 09:55
 **/
public interface CourseMapper extends BaseMapper<Course> {
    public List<Course> selectCourseList(Course course);
}
