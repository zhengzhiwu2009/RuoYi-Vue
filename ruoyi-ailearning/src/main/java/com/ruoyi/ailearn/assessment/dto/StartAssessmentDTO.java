package com.ruoyi.ailearn.assessment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 开始测评请求DTO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@ApiModel(value = "开始测评请求", description = "用户开始测评时提交的参数")
public class StartAssessmentDTO {

    @ApiModelProperty(value = "学生ID", required = true)
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @ApiModelProperty(value = "课程ID", required = true)
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @ApiModelProperty(value = "测评类型：1-章节测评，2-知识点测评", required = true, example = "1")
    @NotNull(message = "测评类型不能为空")
    private Integer assessmentType;

    @ApiModelProperty(value = "章节ID（章节测评时必填）")
    private Long chapterId;

    @ApiModelProperty(value = "知识点ID（知识点测评时必填）")
    private Long kpointId;
}
