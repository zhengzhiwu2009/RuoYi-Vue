package com.ruoyi.ailearn.course.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Chapter实体类（已调整）
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 * @version 2.0
 *
 * 变更说明：
 * 1. 删除masteryKpointNum字段（学生维度的掌握数应该统计student_kpoint_mastery表）
 * 2. 删除kpointTotalNum字段（改为total_kpoints，更清晰的命名）
 * 3. 新增total_kpoints字段（章节总知识点数，固定值）
 * 4. 新增difficulty_level字段（章节难度系数）
 */
@Data
@Accessors(chain = true)
@TableName(value = "chapter", autoResultMap = true)
@ApiModel(value = "Chapter对象", description = "章节")
public class Chapter {

    private static final long serialVersionUID = 1L;

    @TableId(value = "chapterId", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "章节ID")
    private Long chapterId;

    @ApiModelProperty(value = "所属课程ID")
    private Long courseId;

    @ApiModelProperty(value = "章节名称")
    private String name;

    @ApiModelProperty(value = "章节整体状态")
    private Integer state;

    // ==================== 已删除字段 ====================
    // @ApiModelProperty(value = "已掌握知识点数量")
    // @TableField("masteryKpointNum")
    // private Integer masteryKpointNum;
    //
    // @ApiModelProperty(value = "知识点总数")
    // @TableField("kpointTotalNum")
    // private Integer kpointTotalNum;
    //
    // 说明：这两个字段不合理！
    // 因为每个学生的掌握情况不同，不应该存在chapter表！
    // masteryKpointNum应该从student_kpoint_mastery表统计：
    //   SELECT COUNT(*) FROM student_kpoint_mastery
    //   WHERE chapter_id = ? AND student_id = ? AND mastery_level = 'proficient'


    @ApiModelProperty(value = "排序值")
    @TableField("`order`")
    private Integer order;

    @ApiModelProperty(value = "章节编号（如：1.1, 1.2）")
    @TableField("serialNumber")
    private String serialNumber;

    @ApiModelProperty(value = "创建时间（毫秒时间戳）")
    @TableField("createdAt")
    private Long createdAt;

    @ApiModelProperty(value = "知识点列表（JSON字符串）")
    private String kpoints;

    // ==================== 新增字段 ====================

    @ApiModelProperty(value = "章节总知识点数（固定值，不随学生变化，统计该章节下有多少个知识点）")
    @TableField("total_kpoints")
    private Integer totalKpoints;

    @ApiModelProperty(value = "章节难度系数（0.00-1.00，根据该章节下所有知识点的平均难度计算）")
    @TableField("difficulty_level")
    private BigDecimal difficultyLevel;
}
