package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 排名信息VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-26
 *
 * 艹，这个VO用来展示学生在同年级和系统范围内的排名信息！
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "排名信息", description = "学生在同年级和系统范围内的排名")
public class RankingInfoVO {

    @ApiModelProperty(value = "同年级排名（同一学年：9月到次年6月）")
    private Integer classRank;

    @ApiModelProperty(value = "同年级总人数")
    private Integer classTotal;

    @ApiModelProperty(value = "同年级超越百分比（0.00-1.00）")
    private BigDecimal classBeatPercent;

    @ApiModelProperty(value = "系统总排名")
    private Integer systemRank;

    @ApiModelProperty(value = "系统总人数")
    private Integer systemTotal;

    @ApiModelProperty(value = "超越系统百分比（0.00-1.00）")
    private BigDecimal systemBeatPercent;
}
