package com.ruoyi.ailearn.course.controller;

import com.ruoyi.ailearn.assessment.vo.AssessmentReportVO;
import com.ruoyi.ailearn.course.domain.Course;
import com.ruoyi.ailearn.course.model.vo.CourseVO;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ailearn.course.service.CourseService;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 09:32
 **/

@RestController
@RequestMapping("/api/course")
@Api(tags = "课程")
public class CourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询课程列表", notes = "根据条件分页查询课程信息")
    public R<List<Course>> getCoursePage(@RequestBody @ApiParam(value = "分页查询条件", required = true) Course limit) {
        startPage();
        return R.ok(courseService.getCoursePage(limit));
    }

    @GetMapping("/detail")
    @ApiOperation(value = "查询课程详情", notes = "根据课程ID查询课程详细信息")
    public R<CourseVO> getCourseDetail(@RequestParam(value = "courseId") @ApiParam(value = "课程ID", required = true) Long courseId) {
        return R.ok(courseService.getCourseDetail(courseId));
    }
}

