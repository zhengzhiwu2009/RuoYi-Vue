package com.ruoyi.ailearn.assessment.controller;

import com.ruoyi.ailearn.assessment.vo.AnswerFeedbackVO;
import com.ruoyi.ailearn.assessment.vo.AssessmentReportVO;
import com.ruoyi.ailearn.assessment.vo.QuestionVO;
import com.ruoyi.ailearn.assessment.vo.StudentKpointMasteryVO;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ailearn.assessment.dto.StartAssessmentDTO;
import com.ruoyi.ailearn.assessment.dto.SubmitAnswerDTO;
import com.ruoyi.ailearn.assessment.service.AssessmentService;
import com.ruoyi.ailearn.assessment.vo.*;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能测评控制器
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 *
 * 艹，这个Controller包含了整个测评流程的所有接口！
 */
@Slf4j
@RestController
@RequestMapping("/api/assessment")
@Api(tags = "智能测评接口", description = "基于IRT算法的自适应测评系统")
public class AssessmentController extends BaseController {

    @Autowired
    private AssessmentService assessmentService;

    /**
     * 开始测评
     * 创建测评记录并返回第一题
     */
    @PostMapping("/start")
    @ApiOperation(value = "开始测评", notes = "创建测评记录并返回第一题（中等难度0.5）")
    public R<QuestionVO> startAssessment(
            @Validated @RequestBody StartAssessmentDTO dto) {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        log.info("开始测评 - 学生ID:{}, 测评类型:{}, 章节ID:{}, 知识点ID:{}",
            userId, dto.getAssessmentType(),
                dto.getChapterId(), dto.getKpointId());

        try {
            // 参数校验
            if (dto.getAssessmentType() == 1 && dto.getChapterId() == null) {
                return R.fail(500,"章节测评必须提供章节ID");
            }
            if (dto.getAssessmentType() == 2 && dto.getKpointId() == null) {
                return R.fail(500,"知识点测评必须提供知识点ID");
            }

            QuestionVO firstQuestion = assessmentService.startAssessment(dto, userId);
            return R.ok(firstQuestion);

        } catch (Exception e) {
            log.error("开始测评失败", e);
            return R.fail(500,"开始测评失败：" + e.getMessage());
        }
    }

    /**
     * 提交答案
     * 保存答题详情，返回反馈和下一题（或测评报告）
     */
    @PostMapping("/submit")
    @ApiOperation(value = "提交答案", notes = "提交答案后返回反馈信息和下一题，如果测评结束则返回报告")
    public R<AnswerFeedbackVO> submitAnswer(
            @Validated @RequestBody SubmitAnswerDTO dto) {

        log.info("提交答案 - 测评ID:{}, 题目ID:{}, 序号:{}, 答案:{}, 用时:{}秒",
                dto.getAssessmentId(), dto.getQuestionId(),
                dto.getSequence(), dto.getStudentAnswer(), dto.getTimeSpent());

        try {
            AnswerFeedbackVO feedback = assessmentService.submitAnswer(dto);
            return R.ok(feedback);

        } catch (Exception e) {
            log.error("提交答案失败", e);
            return R.fail(500, "提交答案失败：" + e.getMessage());
        }
    }

    /**
     * 获取测评报告
     * 查询已完成测评的详细报告
     */
    @GetMapping("/report/{assessmentId}")
    @ApiOperation(value = "获取测评报告", notes = "查询测评报告详情，包含薄弱知识点、AI评语等")
    public R<AssessmentReportVO> getReport(
            @ApiParam(value = "测评记录ID", required = true)
            @PathVariable Long assessmentId) {

        log.info("获取测评报告 - 测评ID:{}", assessmentId);

        try {
            AssessmentReportVO report = assessmentService.getAssessmentReport(assessmentId);
            return R.ok(report);

        } catch (Exception e) {
            log.error("获取测评报告失败", e);
            return R.fail(500, "获取测评报告失败：" + e.getMessage());
        }
    }

    /**
     * 查询学生测评历史
     * 分页查询学生的历史测评记录
     */
    @GetMapping("/history/{courseId}")
    @ApiOperation(value = "查询测评历史", notes = "分页查询学生的历史测评记录")
    public R<List<AssessmentReportVO>> getHistory(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable(value = "courseId") Long courseId,

            @ApiParam(value = "测评类型：1-章节测评，2-知识点测评")
            @RequestParam(required = false) Integer assessmentType,

            @ApiParam(value = "页码", defaultValue = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @ApiParam(value = "每页数量", defaultValue = "10")
            @RequestParam(defaultValue = "10") Integer size) {

        Long studentId = SecurityUtils.getLoginUser().getUserId();

        log.info("查询测评历史 - 学生ID:{}, 课程ID:{}, 测评类型:{}, 页码:{}, 每页:{}",
                studentId, courseId, assessmentType, page, size);

        try {
            List<AssessmentReportVO> history = assessmentService.getAssessmentHistory(
                    studentId, courseId, assessmentType, page, size);
            return R.ok(history);

        } catch (Exception e) {
            log.error("查询测评历史失败", e);
            return R.fail(500, "查询测评历史失败：" + e.getMessage());
        }
    }

    /**
     * 查询学生知识点掌握情况
     * 查询学生在某个课程下所有知识点的掌握情况
     */
    @GetMapping("/mastery/{studentId}/{courseId}")
    @ApiOperation(value = "查询知识点掌握情况", notes = "查询学生在某个课程下所有知识点的掌握情况")
    public R<List<StudentKpointMasteryVO>> getKpointMastery(
            @ApiParam(value = "学生ID", required = true)
            @PathVariable(value = "studentId") Long studentId,

            @ApiParam(value = "课程ID", required = true)
            @PathVariable(value = "courseId") Long courseId,

            @ApiParam(value = "章节ID")
            @RequestParam(required = false) Long chapterId) {

        log.info("查询知识点掌握情况 - 学生ID:{}, 课程ID:{}, 章节ID:{}",
                studentId, courseId, chapterId);

        try {
            List<StudentKpointMasteryVO> masteryList = assessmentService.getKpointMastery(
                    studentId, courseId, chapterId);
            return R.ok(masteryList);

        } catch (Exception e) {
            log.error("查询知识点掌握情况失败", e);
            return R.fail(500, "查询知识点掌握情况失败：" + e.getMessage());
        }
    }

    /**
     * 查询薄弱知识点
     * 查询学生的薄弱知识点列表
     */
    @GetMapping("/weak-kpoints/{courseId}")
    @ApiOperation(value = "查询薄弱知识点", notes = "查询学生的薄弱知识点列表，按掌握率升序排列")
    public R<List<StudentKpointMasteryVO>> getWeakKpoints(
            @ApiParam(value = "课程ID")
            @PathVariable(value = "courseId") Long courseId,

            @ApiParam(value = "返回数量", defaultValue = "10")
            @RequestParam(defaultValue = "10") Integer limit) {

        Long studentId = SecurityUtils.getLoginUser().getUserId();

        log.info("查询薄弱知识点 - 学生ID:{}, 课程ID:{}, 返回数量:{}",
                studentId, courseId, limit);

        try {
            List<StudentKpointMasteryVO> weakKpoints = assessmentService.getWeakKpoints(
                    studentId, courseId, limit);
            return R.ok(weakKpoints);

        } catch (Exception e) {
            log.error("查询薄弱知识点失败", e);
            return R.fail(500, "查询薄弱知识点失败：" + e.getMessage());
        }
    }

    /**
     * 放弃测评
     * 将未完成的测评标记为已放弃
     */
    @PostMapping("/abandon/{assessmentId}")
    @ApiOperation(value = "放弃测评", notes = "将未完成的测评标记为已放弃（status=2）")
    public R<AssessmentReportVO> abandonAssessment(
            @ApiParam(value = "测评记录ID", required = true)
            @PathVariable Long assessmentId) {

        log.info("放弃测评 - 测评ID:{}", assessmentId);

        try {
            assessmentService.abandonAssessment(assessmentId);
            return R.ok(null);

        } catch (Exception e) {
            log.error("放弃测评失败", e);
            return R.fail(500, "放弃测评失败：" + e.getMessage());
        }
    }
}
