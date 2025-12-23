package com.ruoyi.ailearn.user.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.ailearn.commond.bean.AssignIdBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:28
 **/

@Data
@Accessors(chain = true)
@TableName(value = "learning_trajectory", autoResultMap = true)
@ApiModel(value = "LearningTrajectory对象", description = "用户学习轨迹记录")
public class LearningTrajectory extends AssignIdBaseEntity {

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "知识点ID")
    @TableField("kpoint_id")
    private Long kpointId;

    @ApiModelProperty(value = "当前知识点的练习次数")
    @TableField("practice_count")
    private Integer practiceCount;

    @ApiModelProperty(value = "本次练习完成的题目总数")
    @TableField("total_questions")
    private Integer totalQuestions;

    @ApiModelProperty(value = "本次练习正确率")
    @TableField("correct_rate")
    private Double correctRate;

    @ApiModelProperty(value = "掌握状态：0-未掌握,1-已掌握,2-学习中")
    @TableField("status")
    private int status;

    @ApiModelProperty(value = "学习发生的日期")
    @TableField("study_date")
    private Date studyDate;
}

