package com.ruoyi.ailearn.user.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:58
 **/

@Data
public class LearningTrajectoryDTO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "学科")
    private int category;

    @ApiModelProperty(value = "日期")
    private Date date;
}

