package com.ruoyi.ailearn.course.service;

import com.ruoyi.ailearn.course.domain.Course;
import com.ruoyi.ailearn.course.model.vo.CourseVO;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 09:36
 **/
public interface CourseService {

    /**
     * 获取课程列表
     * @param limit
     * @return
     */
//    PageResult<Course> getCoursePage(Limit<Course> limit);

    List<Course> getCoursePage(Course course);

    /**
     * 获取课程详情
     * @param courseId
     * @return
     */
    CourseVO getCourseDetail(Long courseId);
}
