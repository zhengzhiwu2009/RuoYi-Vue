package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生知识点掌握情况VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "学生知识点掌握情况", description = "学生对某个知识点的掌握情况")
public class StudentKpointMasteryVO {

    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "章节名称")
    private String chapterName;

    @ApiModelProperty(value = "掌握程度：proficient-熟练，basic-基本，not_mastered-未掌握")
    private String masteryLevel;

    @ApiModelProperty(value = "掌握程度文本")
    private String masteryLevelText;

    @ApiModelProperty(value = "掌握率（0.00-1.00）")
    private BigDecimal masteryRate;

    @ApiModelProperty(value = "能力值（0.00-1.00）")
    private BigDecimal abilityScore;

    @ApiModelProperty(value = "累计测评次数")
    private Integer assessmentCount;

    @ApiModelProperty(value = "累计答题数")
    private Integer totalQuestions;

    @ApiModelProperty(value = "累计答对数")
    private Integer correctCount;

    @ApiModelProperty(value = "最近一次得分")
    private Integer lastScore;

    @ApiModelProperty(value = "最高得分")
    private Integer highestScore;

    @ApiModelProperty(value = "首次测评时间")
    private LocalDateTime firstAssessedAt;

    @ApiModelProperty(value = "最近测评时间")
    private LocalDateTime lastAssessedAt;

    @ApiModelProperty(value = "是否为薄弱知识点")
    private Boolean isWeak;

    @ApiModelProperty(value = "连续掌握次数")
    private Integer continuousMasteryCount;

    @ApiModelProperty(value = "进步趋势：improving-进步，stable-稳定，declining-退步")
    private String trend;

    @ApiModelProperty(value = "进步趋势文本")
    private String trendText;
}
