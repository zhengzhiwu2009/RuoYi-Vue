package com.ruoyi.ailearn.course.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.course.domain.Course;
import com.ruoyi.ailearn.course.mapper.CourseMapper;
import com.ruoyi.ailearn.course.service.ChapterService;
import com.ruoyi.ailearn.course.service.CourseService;
import com.ruoyi.ailearn.course.model.vo.CourseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 09:37
 **/

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> getCoursePage(Course course) {
        return courseMapper.selectCourseList(course);
    }


    @Override
    public CourseVO getCourseDetail(Long courseId) {
        Course course = this.getById(courseId);
        if (course == null) {
            return null;
        }

        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(course, courseVO);
        courseVO.setChapterList(chapterService.getChapterList(courseId));
        return courseVO;
    }
}

