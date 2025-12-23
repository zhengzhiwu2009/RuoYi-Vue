package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 知识点表现VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "知识点表现", description = "单个知识点的测评表现")
public class KpointPerformanceVO {

    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "题目数量")
    private Integer questionCount;

    @ApiModelProperty(value = "答对题数")
    private Integer correctCount;

    @ApiModelProperty(value = "答错题数")
    private Integer wrongCount;

    @ApiModelProperty(value = "正确率（0.00-1.00）")
    private BigDecimal accuracy;

    @ApiModelProperty(value = "掌握程度：proficient-熟练，basic-基本，not_mastered-未掌握")
    private String masteryLevel;

    @ApiModelProperty(value = "掌握程度文本")
    private String masteryLevelText;

    @ApiModelProperty(value = "是否为薄弱知识点")
    private Boolean isWeak;
}
