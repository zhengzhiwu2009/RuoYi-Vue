package com.ruoyi.ailearn.course.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: LiuYang
 * @create: 2025-12-04 14:18
 **/

@Data
@Accessors(chain = true)
@TableName(value = "Subject", autoResultMap = true)
@ApiModel(value = "Subject对象", description = "学科")
public class Subject {

    @ApiModelProperty(value = "学科ID")
    @TableId(value = "category", type = IdType.ASSIGN_ID)
    private Integer category;

    @ApiModelProperty(value = "学科名称")
    @TableField("name")
    private String name;
}

