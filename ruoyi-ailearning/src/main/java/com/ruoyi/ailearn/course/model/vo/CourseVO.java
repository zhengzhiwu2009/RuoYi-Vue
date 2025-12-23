package com.ruoyi.ailearn.course.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 11:01
 **/

@Data
@Accessors(chain = true)
@ApiModel(value = "CourseVO", description = "课程视图对象")
public class CourseVO {

    @ApiModelProperty(value = "课程ID")
    private Long id;

    @ApiModelProperty(value = "课程分类名称")
    private String categoryName;

    @ApiModelProperty(value = "出版社名称")
    private String pressName;

    @ApiModelProperty(value = "年级名称")
    private String gradeName;

    @ApiModelProperty(value = "章节列表")
    private List<ChapterVO> chapterList;
}

