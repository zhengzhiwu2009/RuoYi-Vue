package com.ruoyi.ailearn.course.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 11:11
 **/

@Data
@Accessors(chain = true)
@ApiModel(value = "ChapterVO", description = "章节视图对象")
public class ChapterVO {

    @ApiModelProperty(value = "章节ID")
    private Long chapterId;

    @ApiModelProperty(value = "章节名称")
    private String name;

    @ApiModelProperty(value = "按钮类型（用于前端交互逻辑）")
    private Integer buttonType;

    @ApiModelProperty(value = "已掌握知识点数量")
    private Integer masteryKpointNum;

    @ApiModelProperty(value = "知识点总数")
    private Integer kpointTotalNum;

    @ApiModelProperty(value = "下一知识点ID")
    private Long nextKpointId;

    @ApiModelProperty(value = "下一知识点名称")
    private String nextKpointName;

    @ApiModelProperty(value = "章节学习状态（如：0-锁定，1-可学，2-已完成）")
    private Integer chapterState;

    @ApiModelProperty(value = "知识点列表")
    private List<KpointVO> kpointList;
}

