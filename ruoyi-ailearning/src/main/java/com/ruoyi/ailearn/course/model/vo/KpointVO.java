package com.ruoyi.ailearn.course.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 11:09
 **/

@Data
@Accessors(chain = true)
@ApiModel(value = "KpointVO", description = "知识点视图对象")
public class KpointVO {

    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "章节ID")
    private Long chapterId;

    @ApiModelProperty(value = "课程Id")
    private Long courseId;

    @ApiModelProperty(value = "知识点名称")
    private String name;

    @ApiModelProperty(value = "视频")
    private VideoItem video;

    @ApiModelProperty(value = "掌握率")
    private Double masteryRate;

    @ApiModelProperty(value = "题目列表")
    List<QuestionVO> questionList;

    @ApiModelProperty(hidden = true)
    private String videoJSON; // 原始 JSON 字符串，不暴露给前端
}

