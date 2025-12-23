package com.ruoyi.ailearn.course.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Kpoint实体类（已调整）
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 * @version 2.0
 *
 * 变更说明：
 * 1. 删除masteryRate字段（学生维度的掌握率应该在student_kpoint_mastery表）
 * 2. 新增difficulty_level字段（知识点难度系数）
 * 3. 新增question_count字段（关联题目数量）
 * 4. 新增prerequisite_kpoints字段（前置知识点ID列表）
 */
@Data
@Accessors(chain = true)
@TableName(value = "kpoint", autoResultMap = true)
@ApiModel(value = "Kpoint对象", description = "知识点")
public class Kpoint {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属章节ID")
    @TableField("chapterId")
    private Long chapterId;

    @TableId(value = "kpointId", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "密文内容（加密数据）")
    private String ciphertext;

    @ApiModelProperty(value = "知识点名称")
    private String name;

    @ApiModelProperty(value = "日期信息（字符串格式，如 '2025-12-04'）")
    private String date;

    // ==================== 已删除字段 ====================
    // @ApiModelProperty(value = "掌握率（0.0 ~ 1.0）")
    // @TableField("masteryRate")
    // private Double masteryRate;
    // 说明：每个学生的掌握率不同，不应该存在kpoint表！
    // 应该从student_kpoint_mastery表查询

    @ApiModelProperty(value = "媒体信息（JSON 或其他结构化文本）")
    @TableField("mediaInfo")
    private String mediaInfo;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "返回消息（用于调试或提示）")
    @TableField("retMsg")
    private String retMsg;

    @ApiModelProperty(value = "视频")
    private String video;

    @ApiModelProperty(value = "类型（如：1-视频，2-练习等）")
    private Integer type;

    // ==================== 新增字段 ====================

    @ApiModelProperty(value = "知识点难度系数（0.00-1.00，根据该知识点下所有题目的平均难度计算）")
    @TableField("difficulty_level")
    private BigDecimal difficultyLevel;

    @ApiModelProperty(value = "关联题目数量（用于快速统计，避免每次都JOIN查询）")
    @TableField("question_count")
    private Integer questionCount;

    @ApiModelProperty(value = "前置知识点ID列表（JSON数组，如：[123,456,789]，用于构建知识图谱）")
    @TableField("prerequisite_kpoints")
    private String prerequisiteKpoints;
}
