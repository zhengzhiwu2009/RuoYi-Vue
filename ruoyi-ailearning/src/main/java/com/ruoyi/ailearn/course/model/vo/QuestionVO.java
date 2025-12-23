package com.ruoyi.ailearn.course.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-02 10:09
 **/

@Data
@ApiModel(value = "QuestionVO视图")
public class QuestionVO {

    @ApiModelProperty(value = "题目ID")
    private Long id;

    @ApiModelProperty(value = "题目内容（HTML格式）")
    @TableField("problem")
    private String content;

    @ApiModelProperty(value = "选项")
    private List<String> options;

    @ApiModelProperty(value = "答案")
    private List<AnswerItem> answer;

    @ApiModelProperty(value = "题目类型：0-选择题，1-填空题")
    private Integer type;

    @ApiModelProperty(value = "解析说明（HTML格式）")
    private String explanation;


    @ApiModelProperty(hidden = true)
    @TableField("options")
    private String optionsJson; // 原始 JSON 字符串，不暴露给前端

    @ApiModelProperty(hidden = true)
    @TableField("answer_items")
    private String answerJson; // 原始 JSON 字符串，不暴露给前端
}

