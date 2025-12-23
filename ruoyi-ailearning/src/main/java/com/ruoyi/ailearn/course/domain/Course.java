package com.ruoyi.ailearn.course.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: LiuYang
 * @create: 2025-12-04 09:47
 **/

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "course", autoResultMap = true)
@ApiModel(value = "Course对象", description = "课程")
public class Course {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "课程分类名称")
    private String categoryName;

    @ApiModelProperty(value = "出版社名称")
    private String pressName;

    @ApiModelProperty(value = "年级名称")
    private String gradeName;

    @ApiModelProperty(value = "课程图片URL")
    private String imageUrl;

    @ApiModelProperty(value = "课程名称")
    private String name;

    @ApiModelProperty(value = "来源（如：1-系统内置，2-用户上传等）")
    private Integer source;

    @ApiModelProperty(value = "排序字段")
    @TableField(value = "`order`")
    private Long order;

    @ApiModelProperty(value = "机构ID")
    private Long orgId;

    @ApiModelProperty(value = "教室/直播间ID")
    private Long roomId;

    @ApiModelProperty(value = "章节名称")
    private String chapterName;

    @ApiModelProperty(value = "课程分类ID")
    private Integer category;

    @ApiModelProperty(value = "出版社ID")
    private Integer press;

    @ApiModelProperty(value = "年级ID")
    private Integer grade;

    @ApiModelProperty(value = "状态（如：0-草稿，1-已发布等）")
    private Integer state;

    @ApiModelProperty(value = "教师姓名")
    private String teacherName;

    @ApiModelProperty(value = "知识点总数")
    private Integer kpointTotalNum;

    @ApiModelProperty(value = "已掌握知识点数量")
    private Integer masteryKpointNum;

    @ApiModelProperty(value = "封面图")
    private String cover;

    @ApiModelProperty(value = "创建人")
    private String createdBy;

    @ApiModelProperty(value = "是否过期")
    private Boolean isExpired;

    @ApiModelProperty(value = "区域名称（如：省市区）")
    private String districtName;

    @ApiModelProperty(value = "是否付费课程")
    private Boolean isPay;
}

