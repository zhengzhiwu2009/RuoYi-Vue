package com.ruoyi.ailearn.assessment.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.ailearn.commond.bean.AssignIdBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答题详情实体类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "answer_detail", autoResultMap = true)
@ApiModel(value = "AnswerDetail对象", description = "答题详情")
public class AnswerDetail extends AssignIdBaseEntity {

    @ApiModelProperty(value = "测评记录ID")
    @TableField("assessment_id")
    private Long assessmentId;

    @ApiModelProperty(value = "题目ID")
    @TableField("question_id")
    private Long questionId;

    @ApiModelProperty(value = "知识点ID")
    @TableField("kpoint_id")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称（冗余字段，方便查询）")
    @TableField("kpoint_name")
    private String kpointName;

    @ApiModelProperty(value = "题目序号（第几题）")
    @TableField("sequence")
    private Integer sequence;

    @ApiModelProperty(value = "题目难度（0.00-1.00）")
    @TableField("difficulty")
    private BigDecimal difficulty;

    @ApiModelProperty(value = "题目区分度（IRT参数a）")
    @TableField("discrimination")
    private BigDecimal discrimination;

    @ApiModelProperty(value = "题目猜测度（IRT参数c，仅用于测试，数据库中不存储）")
    @TableField(exist = false)
    private BigDecimal guessing;

    @ApiModelProperty(value = "是否答对")
    @TableField("is_correct")
    private Boolean isCorrect;

    @ApiModelProperty(value = "学生答案")
    @TableField("student_answer")
    private String studentAnswer;

    @ApiModelProperty(value = "正确答案")
    @TableField("correct_answer")
    private String correctAnswer;

    @ApiModelProperty(value = "答题用时（秒）")
    @TableField("time_spent")
    private Integer timeSpent;

    @ApiModelProperty(value = "标准用时（秒）")
    @TableField("standard_time")
    private Integer standardTime;

    @ApiModelProperty(value = "答题时间")
    @TableField("answered_at")
    private LocalDateTime answeredAt;

    @ApiModelProperty(value = "答题速度得分（0.00-1.00）")
    @TableField("speed_score")
    private BigDecimal speedScore;

    @ApiModelProperty(value = "综合得分（0.00-1.00）")
    @TableField("item_score")
    private BigDecimal itemScore;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
