package com.ruoyi.ailearn.assessment.service;

import com.ruoyi.ailearn.assessment.dto.StartAssessmentDTO;
import com.ruoyi.ailearn.assessment.dto.SubmitAnswerDTO;
import com.ruoyi.ailearn.assessment.vo.AnswerFeedbackVO;
import com.ruoyi.ailearn.assessment.vo.AssessmentReportVO;
import com.ruoyi.ailearn.assessment.vo.QuestionVO;
import com.ruoyi.ailearn.assessment.vo.StudentKpointMasteryVO;
import com.ruoyi.ailearn.assessment.vo.*;

import java.util.List;

/**
 * 测评服务接口
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 *
 * 艹，这是整个测评系统的核心业务接口！
 */
public interface AssessmentService {

    /**
     * 开始测评 - 创建测评记录并返回第1题
     *
     * @param dto 开始测评请求
     * @return 第一题
     */
    QuestionVO startAssessment(StartAssessmentDTO dto, Long userId);

    /**
     * 提交答案 - 保存答题详情并返回反馈（含下一题或报告）
     *
     * @param dto 提交答案请求
     * @return 答题反馈信息
     */
    AnswerFeedbackVO submitAnswer(SubmitAnswerDTO dto);

    /**
     * 获取测评报告
     *
     * @param assessmentId 测评记录ID
     * @return 测评报告
     */
    AssessmentReportVO getAssessmentReport(Long assessmentId);

    /**
     * 查询测评历史
     *
     * @param studentId      学生ID
     * @param courseId       课程ID（可选）
     * @param assessmentType 测评类型（可选）
     * @param page           页码
     * @param size           每页数量
     * @return 测评历史列表
     */
    List<AssessmentReportVO> getAssessmentHistory(Long studentId,
                                                   Long courseId,
                                                   Integer assessmentType,
                                                   Integer page,
                                                   Integer size);

    /**
     * 查询知识点掌握情况
     *
     * @param studentId 学生ID
     * @param courseId  课程ID
     * @param chapterId 章节ID（可选）
     * @return 知识点掌握情况列表
     */
    List<StudentKpointMasteryVO> getKpointMastery(Long studentId,
                                                   Long courseId,
                                                   Long chapterId);

    /**
     * 查询薄弱知识点
     *
     * @param studentId 学生ID
     * @param courseId  课程ID（可选）
     * @param limit     返回数量
     * @return 薄弱知识点列表
     */
    List<StudentKpointMasteryVO> getWeakKpoints(Long studentId,
                                                 Long courseId,
                                                 Integer limit);

    /**
     * 放弃测评
     *
     * @param assessmentId 测评记录ID
     */
    void abandonAssessment(Long assessmentId);
}
