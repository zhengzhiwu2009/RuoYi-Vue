package com.ruoyi.ailearn.course.controller;

import com.ruoyi.ailearn.assessment.vo.AssessmentReportVO;
import com.ruoyi.ailearn.course.domain.Subject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ailearn.course.service.SubjectService;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 14:23
 **/

@RestController
@RequestMapping("/api/subject")
@Api(tags = "学科")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @RequestMapping("/all")
    public R<List<Subject>> getAllSubjects() {
        return R.ok(subjectService.getAllSubjects());
    }
}

