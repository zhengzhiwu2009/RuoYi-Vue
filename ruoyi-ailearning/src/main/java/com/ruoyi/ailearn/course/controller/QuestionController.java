package com.ruoyi.ailearn.course.controller;

import com.ruoyi.ailearn.assessment.vo.AssessmentReportVO;
import com.ruoyi.ailearn.course.model.vo.KpointMasteryVO;
import com.ruoyi.ailearn.course.model.vo.QuestionVO;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ailearn.course.service.QuestionService;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-02 09:50
 **/

@RestController
@RequestMapping("/api/question")
@Api(tags = "题目")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/list")
    public R<List<QuestionVO>> getQuestionList(@RequestParam(value = "kpointId") Long kpointId) {
        return R.ok(questionService.getQuestionList(kpointId));
    }

}

