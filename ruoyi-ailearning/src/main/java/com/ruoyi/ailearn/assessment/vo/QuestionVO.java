package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 题目VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "题目信息", description = "返回给前端的题目信息")
public class QuestionVO {

    @ApiModelProperty(value = "题目ID")
    private Long questionId;

    @ApiModelProperty(value = "题目序号（第几题）")
    private Integer sequence;

    @ApiModelProperty(value = "题目内容")
    private String problem;

    @ApiModelProperty(value = "答案(调试期结束后删除)")
    private String answerItems;

    @ApiModelProperty(value = "题目类型：1-单选，2-多选，3-判断，4-填空")
    private Integer type;

    @ApiModelProperty(value = "选项（JSON数组）")
    private String options;

    @ApiModelProperty(value = "题目难度（0.00-1.00）")
    private BigDecimal difficulty;

    @ApiModelProperty(value = "标准答题时间（秒）")
    private Integer standardTime;

    @ApiModelProperty(value = "进度提示：第x题/共6-10题")
    private String progress;

    @ApiModelProperty(value = "知识点ID")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "测评id")
    private Long assessmentId;
}
