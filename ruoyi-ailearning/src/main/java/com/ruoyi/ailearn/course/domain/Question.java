package com.ruoyi.ailearn.course.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.ailearn.commond.bean.AssignIdBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Question实体类（已调整，支持IRT算法）
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 * @version 2.0
 *
 * 变更说明：
 * 1. 新增difficulty字段（IRT难度值0-1）
 * 2. 新增discrimination字段（IRT区分度）
 * 3. 新增guessing字段（IRT猜测度）
 * 4. 新增avg_time字段（平均答题时间）
 * 5. 新增quality_score字段（题目质量分数）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "question", autoResultMap = true)
@ApiModel(value = "Question对象", description = "问题")
public class Question extends AssignIdBaseEntity {

    @ApiModelProperty(value = "题目内容")
    private String problem;

    @ApiModelProperty(value = "选项")
    private String options;

    @ApiModelProperty(value = "解释说明")
    private String explanation;

    @ApiModelProperty(value = "知识点ID")
    private Long kpId;

    @ApiModelProperty(value = "知识点ID（冗余字段）")
    private Long kpointId;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "题目类型")
    private Integer type;

    @ApiModelProperty(value = "是否完成")
    private Integer isFinish;

    @ApiModelProperty(value = "来源")
    private Integer sourceOf;

    @ApiModelProperty(value = "父级ID")
    private Long parentId;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "未完成正确数量")
    private Integer incompleteCorrectNumber;

    @ApiModelProperty(value = "音频脚本")
    private String audioScript;

    @ApiModelProperty(value = "错误答案列表")
    private String errorAnswerList;

    @ApiModelProperty(value = "排序")
    @TableField(value = "`order`")
    private Integer order;

    @ApiModelProperty(value = "答案字符串")
    private String answerStr;

    @ApiModelProperty(value = "答案项")
    private String answerItems;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "含义")
    private String meaning;

    @ApiModelProperty(value = "密文")
    private String ciphertext;

    @ApiModelProperty(value = "总正确数")
    private Integer totalCorrect;

    @ApiModelProperty(value = "总错误数")
    private Integer totalError;

    @ApiModelProperty(value = "配置参数")
    private String configPara;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "错误率")
    private BigDecimal errorRate;

    @ApiModelProperty(value = "错误率（冗余字段）")
    private BigDecimal wrongRate;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "创建人")
    private Long createdBy;

    @ApiModelProperty(value = "更新人")
    private Long updatedBy;

    @ApiModelProperty(value = "子题目")
    private String childQuestions;

    @ApiModelProperty(value = "子题目列表")
    private String childQuestionList;

    @ApiModelProperty(value = "URL地址")
    private String url;

    @ApiModelProperty(value = "是否正确")
    private Boolean isCorrect;

    @ApiModelProperty(value = "数学答案映射")
    private String answerMathMap;

    @ApiModelProperty(value = "绑定知识点ID")
    private Long bindKpointId;

    @ApiModelProperty(value = "绑定知识点名称")
    private String bindKpointName;

    @ApiModelProperty(value = "多选题答案项")
    private String multipleChoiceAnswerItems;

    @ApiModelProperty(value = "正确类型")
    private Integer correctType;

    @ApiModelProperty(value = "难度等级（业务层面：1-简单，2-较简单，3-中等，4-较难，5-困难）")
    private Integer difficult;

    @ApiModelProperty(value = "分类")
    private Integer category;

    @ApiModelProperty(value = "描述")
    private String des;

    @ApiModelProperty(value = "原始内容")
    private String original;

    @ApiModelProperty(value = "题目来源")
    private String queSource;

    @ApiModelProperty(value = "删除问题标记")
    private Integer delQuestion;

    @ApiModelProperty(value = "审核状态")
    private Integer examinestate;

    @ApiModelProperty(value = "检查者角色")
    private String checkerRole;

    @ApiModelProperty(value = "状态")
    private Integer status;

    // ==================== 新增字段（IRT算法专用）====================

    @ApiModelProperty(value = "题目难度（IRT参数b，0.00-1.00，用于自适应算法）")
    @TableField("difficulty")
    private BigDecimal difficulty;

    @ApiModelProperty(value = "题目区分度（IRT参数a，建议1.0-2.5，值越大区分能力越强）")
    @TableField("discrimination")
    private BigDecimal discrimination;

    @ApiModelProperty(value = "猜测度（IRT参数c，一般0.2-0.3，选择题的猜对概率）")
    @TableField("guessing")
    private BigDecimal guessing;

    @ApiModelProperty(value = "平均答题时间（秒，基于历史答题数据统计）")
    @TableField("avg_time")
    private Integer avgTime;

    @ApiModelProperty(value = "题目质量分数（0-100，基于多维度评估：区分度、正确率分布、用时稳定性等）")
    @TableField("quality_score")
    private Integer qualityScore;
}
