package com.ruoyi.ailearn.course.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 知识点掌握情况详细VO
 * 艹！这个VO包含了知识点的所有掌握数据，别tm乱用！
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-18
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "KpointMasteryVO", description = "知识点掌握情况详细视图对象")
public class KpointMasteryVO {

    // ==================== 知识点基本信息 ====================

    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "章节ID")
    private Long chapterId;

    @ApiModelProperty(value = "章节名称")
    private String chapterName;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    // ==================== 掌握情况核心数据 ====================

    @ApiModelProperty(value = "掌握程度：proficient-熟练掌握，basic-基本掌握，not_mastered-未掌握")
    private String masteryLevel;

    @ApiModelProperty(value = "掌握率（0.00-1.00）")
    private BigDecimal masteryRate;

    @ApiModelProperty(value = "IRT能力值（0.00-1.00）")
    private BigDecimal abilityScore;

    // ==================== 测评统计数据 ====================

    @ApiModelProperty(value = "累计测评次数")
    private Integer assessmentCount;

    @ApiModelProperty(value = "累计答题数")
    private Integer totalQuestions;

    @ApiModelProperty(value = "累计答对数")
    private Integer correctCount;

    @ApiModelProperty(value = "累计答错数")
    private Integer wrongCount;

    @ApiModelProperty(value = "正确率（百分比，如：75.5表示75.5%）")
    private BigDecimal correctRate;

    // ==================== 得分数据 ====================

    @ApiModelProperty(value = "最近一次测评得分（0-100）")
    private Integer lastScore;

    @ApiModelProperty(value = "最高测评得分（0-100）")
    private Integer highestScore;

    // ==================== 分析数据 ====================

    @ApiModelProperty(value = "是否为薄弱知识点")
    private Boolean isWeak;

    @ApiModelProperty(value = "连续掌握次数（连续测评都达到熟练）")
    private Integer continuousMasteryCount;

    @ApiModelProperty(value = "进步趋势：improving-进步中，stable-稳定，declining-退步")
    private String trend;

    // ==================== 时间数据 ====================

    @ApiModelProperty(value = "首次测评时间")
    private LocalDateTime firstAssessedAt;

    @ApiModelProperty(value = "最近测评时间")
    private LocalDateTime lastAssessedAt;

    // ==================== 状态标识 ====================

    @ApiModelProperty(value = "是否已测评过（有掌握数据）")
    private Boolean hasAssessed;
}
