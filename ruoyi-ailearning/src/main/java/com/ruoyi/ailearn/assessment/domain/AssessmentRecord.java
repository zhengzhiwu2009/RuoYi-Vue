package com.ruoyi.ailearn.assessment.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.ailearn.commond.bean.AssignIdBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测评记录实体类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "assessment_record", autoResultMap = true)
@ApiModel(value = "AssessmentRecord对象", description = "测评记录")
public class AssessmentRecord extends AssignIdBaseEntity {

    @ApiModelProperty(value = "学生ID")
    @TableField("student_id")
    private Long studentId;

    @ApiModelProperty(value = "课程ID")
    @TableField("course_id")
    private Long courseId;

    @ApiModelProperty(value = "测评类型：1-章节测评，2-知识点测评")
    @TableField("assessment_type")
    private Integer assessmentType;

    @ApiModelProperty(value = "章节ID（章节测评时必填）")
    @TableField("chapter_id")
    private Long chapterId;

    @ApiModelProperty(value = "知识点ID（知识点测评时必填）")
    @TableField("kpoint_id")
    private Long kpointId;

    @ApiModelProperty(value = "总题数")
    @TableField("total_questions")
    private Integer totalQuestions;

    @ApiModelProperty(value = "答对题数")
    @TableField("correct_count")
    private Integer correctCount;

    @ApiModelProperty(value = "答错题数")
    @TableField("wrong_count")
    private Integer wrongCount;

    @ApiModelProperty(value = "正确率（0.00-1.00）")
    @TableField("accuracy")
    private BigDecimal accuracy;

    @ApiModelProperty(value = "IRT能力值（0.00-1.00）")
    @TableField("ability_score")
    private BigDecimal abilityScore;

    @ApiModelProperty(value = "信心水平（0.00-1.00）")
    @TableField("confidence_level")
    private BigDecimal confidenceLevel;

    @ApiModelProperty(value = "掌握程度：proficient-熟练掌握，basic-基本掌握，not_mastered-未掌握")
    @TableField("mastery_level")
    private String masteryLevel;

    @ApiModelProperty(value = "综合得分（0-100）")
    @TableField("final_score")
    private Integer finalScore;

    @ApiModelProperty(value = "总用时（秒）")
    @TableField("total_time")
    private Integer totalTime;

    @ApiModelProperty(value = "平均每题用时（秒）")
    @TableField("avg_time_per_question")
    private Integer avgTimePerQuestion;

    @ApiModelProperty(value = "开始时间")
    @TableField("started_at")
    private LocalDateTime startedAt;

    @ApiModelProperty(value = "完成时间")
    @TableField("completed_at")
    private LocalDateTime completedAt;

    @ApiModelProperty(value = "AI生成的个性化评语")
    @TableField("ai_comment")
    private String aiComment;

    @ApiModelProperty(value = "薄弱知识点分析（JSON数组）")
    @TableField("weak_kpoints")
    private String weakKpoints;

    @ApiModelProperty(value = "已掌握知识点列表（JSON数组）")
    @TableField("mastered_kpoints")
    private String masteredKpoints;

    @ApiModelProperty(value = "测评状态：0-进行中，1-已完成，2-已放弃")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
