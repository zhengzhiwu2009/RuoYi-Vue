package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 答题反馈VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "答题反馈信息", description = "提交答案后返回的反馈信息")
public class AnswerFeedbackVO {

    @ApiModelProperty(value = "是否答对")
    private Boolean isCorrect;

    @ApiModelProperty(value = "正确答案")
    private String correctAnswer;

    @ApiModelProperty(value = "题目解析")
    private String explanation;

    @ApiModelProperty(value = "当前能力估计值（0.00-1.00）")
    private BigDecimal currentAbility;

    @ApiModelProperty(value = "当前正确率")
    private BigDecimal currentAccuracy;

    @ApiModelProperty(value = "已答题数")
    private Integer answeredCount;

    @ApiModelProperty(value = "下一题信息（如果测评未结束）")
    private QuestionVO nextQuestion;

    @ApiModelProperty(value = "测评是否结束")
    private Boolean isFinished;

    @ApiModelProperty(value = "测评报告（如果已结束）")
    private AssessmentReportVO report;
}
