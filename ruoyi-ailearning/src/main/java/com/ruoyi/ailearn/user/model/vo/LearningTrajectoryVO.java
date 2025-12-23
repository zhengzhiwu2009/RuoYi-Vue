package com.ruoyi.ailearn.user.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: LiuYang
 * @create: 2025-12-11 16:35
 **/

@Data
public class LearningTrajectoryVO {

    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "当前知识点的练习次数")
    private Integer practiceCount;

    @ApiModelProperty(value = "本次练习完成的题目总数")
    private Integer totalQuestions;

    @ApiModelProperty(value = "本次练习正确率")
    private Double correctRate;

    @ApiModelProperty(value = "掌握状态：0-未掌握,1-已掌握,2-学习中")
    private int status;

    @ApiModelProperty(value = "学习发生的日期")
    private Date studyDate;
}

