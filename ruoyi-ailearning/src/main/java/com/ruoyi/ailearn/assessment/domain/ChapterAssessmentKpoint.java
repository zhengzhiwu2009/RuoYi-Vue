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
 * 章节测评涉及知识点实体类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "chapter_assessment_kpoint", autoResultMap = true)
@ApiModel(value = "ChapterAssessmentKpoint对象", description = "章节测评知识点明细")
public class ChapterAssessmentKpoint extends AssignIdBaseEntity {

    @ApiModelProperty(value = "测评记录ID")
    @TableField("assessment_id")
    private Long assessmentId;

    @ApiModelProperty(value = "章节ID")
    @TableField("chapter_id")
    private Long chapterId;

    @ApiModelProperty(value = "知识点ID")
    @TableField("kpoint_id")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    @TableField("kpoint_name")
    private String kpointName;

    @ApiModelProperty(value = "本知识点题目数")
    @TableField("question_count")
    private Integer questionCount;

    @ApiModelProperty(value = "答对题数")
    @TableField("correct_count")
    private Integer correctCount;

    @ApiModelProperty(value = "答错题数")
    @TableField("wrong_count")
    private Integer wrongCount;

    @ApiModelProperty(value = "正确率（0.00-1.00）")
    @TableField("accuracy")
    private BigDecimal accuracy;

    @ApiModelProperty(value = "掌握程度：proficient-熟练，basic-基本，not_mastered-未掌握")
    @TableField("mastery_level")
    private String masteryLevel;

    @ApiModelProperty(value = "是否为薄弱知识点")
    @TableField("is_weak")
    private Boolean isWeak;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
