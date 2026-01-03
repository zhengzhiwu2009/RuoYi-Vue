package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 历史趋势数据VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-26
 *
 * 艹，这个VO用来展示学生的历史测评趋势，前端画折线图用的！
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "历史趋势数据", description = "学生历史测评的趋势数据")
public class HistoryTrendVO {

    @ApiModelProperty(value = "测评日期")
    private LocalDateTime date;

    @ApiModelProperty(value = "得分（0-100）")
    private Integer score;

    @ApiModelProperty(value = "能力值（0.00-1.00）")
    private BigDecimal ability;

    @ApiModelProperty(value = "正确率（0.00-1.00）")
    private BigDecimal accuracy;

    @ApiModelProperty(value = "测评ID")
    private Long assessmentId;
}
