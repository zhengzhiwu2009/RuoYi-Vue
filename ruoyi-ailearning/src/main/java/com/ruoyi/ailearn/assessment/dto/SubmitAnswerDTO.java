package com.ruoyi.ailearn.assessment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 提交答案请求DTO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@ApiModel(value = "提交答案请求", description = "学生答题后提交答案的参数")
public class SubmitAnswerDTO {

    @ApiModelProperty(value = "测评记录ID", required = true)
    @NotNull(message = "测评记录ID不能为空")
    private Long assessmentId;

    @ApiModelProperty(value = "题目ID", required = true)
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @ApiModelProperty(value = "题目序号（第几题）", required = true, example = "1")
    @NotNull(message = "题目序号不能为空")
    @Min(value = 1, message = "题目序号必须大于0")
    private Integer sequence;

    @ApiModelProperty(value = "学生答案", required = true, example = "A")
    @NotBlank(message = "学生答案不能为空")
    private String studentAnswer;

    @ApiModelProperty(value = "答题用时（秒）", required = true, example = "72")
    @NotNull(message = "答题用时不能为空")
    @Min(value = 1, message = "答题用时必须大于0")
    private Integer timeSpent;
}
