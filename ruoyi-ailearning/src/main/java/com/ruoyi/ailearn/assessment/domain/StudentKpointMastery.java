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
 * 学生知识点掌握情况实体类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "student_kpoint_mastery", autoResultMap = true)
@ApiModel(value = "StudentKpointMastery对象", description = "学生知识点掌握情况")
public class StudentKpointMastery extends AssignIdBaseEntity {

    @ApiModelProperty(value = "学生ID")
    @TableField("student_id")
    private Long studentId;

    @ApiModelProperty(value = "课程ID")
    @TableField("course_id")
    private Long courseId;

    @ApiModelProperty(value = "章节ID")
    @TableField("chapter_id")
    private Long chapterId;

    @ApiModelProperty(value = "知识点ID")
    @TableField("kpoint_id")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称（冗余字段）")
    @TableField("kpoint_name")
    private String kpointName;

    @ApiModelProperty(value = "掌握程度：proficient-熟练掌握，basic-基本掌握，not_mastered-未掌握")
    @TableField("mastery_level")
    private String masteryLevel;

    @ApiModelProperty(value = "掌握率（0.00-1.00）")
    @TableField("mastery_rate")
    private BigDecimal masteryRate;

    @ApiModelProperty(value = "IRT能力值（0.00-1.00）")
    @TableField("ability_score")
    private BigDecimal abilityScore;

    @ApiModelProperty(value = "累计测评次数")
    @TableField("assessment_count")
    private Integer assessmentCount;

    @ApiModelProperty(value = "累计答题数")
    @TableField("total_questions")
    private Integer totalQuestions;

    @ApiModelProperty(value = "累计答对数")
    @TableField("correct_count")
    private Integer correctCount;

    @ApiModelProperty(value = "累计答错数")
    @TableField("wrong_count")
    private Integer wrongCount;

    @ApiModelProperty(value = "最近一次测评得分（0-100）")
    @TableField("last_score")
    private Integer lastScore;

    @ApiModelProperty(value = "最高测评得分（0-100）")
    @TableField("highest_score")
    private Integer highestScore;

    @ApiModelProperty(value = "首次测评时间")
    @TableField("first_assessed_at")
    private LocalDateTime firstAssessedAt;

    @ApiModelProperty(value = "最近测评时间")
    @TableField("last_assessed_at")
    private LocalDateTime lastAssessedAt;

    @ApiModelProperty(value = "是否为薄弱知识点")
    @TableField("is_weak")
    private Boolean isWeak;

    @ApiModelProperty(value = "连续掌握次数（连续测评都达到熟练）")
    @TableField("continuous_mastery_count")
    private Integer continuousMasteryCount;

    @ApiModelProperty(value = "进步趋势：improving-进步中，stable-稳定，declining-退步")
    @TableField("trend")
    private String trend;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
