package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答题详情VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "答题详情", description = "单道题的答题详情")
public class AnswerDetailVO {

    @ApiModelProperty(value = "题目序号")
    private Integer sequence;

    @ApiModelProperty(value = "题目ID")
    private Long questionId;

    @ApiModelProperty(value = "题目内容")
    private String problem;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "题目难度（0.00-1.00）")
    private BigDecimal difficulty;

    @ApiModelProperty(value = "是否答对")
    private Boolean isCorrect;

    @ApiModelProperty(value = "学生答案")
    private String studentAnswer;

    @ApiModelProperty(value = "正确答案")
    private String correctAnswer;

    @ApiModelProperty(value = "答题用时（秒）")
    private Integer timeSpent;

    @ApiModelProperty(value = "标准用时（秒）")
    private Integer standardTime;

    @ApiModelProperty(value = "答题时间")
    private LocalDateTime answeredAt;

    @ApiModelProperty(value = "题目解析")
    private String explanation;
}
