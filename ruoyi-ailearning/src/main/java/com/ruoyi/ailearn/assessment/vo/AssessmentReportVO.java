package com.ruoyi.ailearn.assessment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测评报告VO
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "测评报告", description = "测评完成后的详细报告")
public class AssessmentReportVO {

    @ApiModelProperty(value = "测评记录ID")
    private Long assessmentId;

    @ApiModelProperty(value = "测评类型：1-章节测评，2-知识点测评")
    private Integer assessmentType;

    @ApiModelProperty(value = "章节名称")
    private String chapterName;

    @ApiModelProperty(value = "知识点名称")
    private String kpointName;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime completedAt;

    // ========== 测评概况 ==========
    @ApiModelProperty(value = "总题数")
    private Integer totalQuestions;

    @ApiModelProperty(value = "答对题数")
    private Integer correctCount;

    @ApiModelProperty(value = "答错题数")
    private Integer wrongCount;

    @ApiModelProperty(value = "正确率（0.00-1.00）")
    private BigDecimal accuracy;

    @ApiModelProperty(value = "总用时（秒）")
    private Integer totalTime;

    @ApiModelProperty(value = "平均每题用时（秒）")
    private Integer avgTime;

    @ApiModelProperty(value = "最快答题时间（秒）")
    private Integer minTime;

    @ApiModelProperty(value = "最慢答题时间（秒）")
    private Integer maxTime;

    @ApiModelProperty(value = "该单元所有用户平均测评总用时（秒）")
    private Integer avgTimeStandard;

    // ========== 掌握程度评定 ==========
    @ApiModelProperty(value = "能力估计值（0.00-1.00）")
    private BigDecimal abilityScore;

    @ApiModelProperty(value = "信心水平（0.00-1.00）")
    private BigDecimal confidenceLevel;

    @ApiModelProperty(value = "掌握程度：proficient-熟练掌握，basic-基本掌握，not_mastered-未掌握")
    private String masteryLevel;

    @ApiModelProperty(value = "掌握程度文本")
    private String masteryLevelText;

    @ApiModelProperty(value = "综合得分（0-100）")
    private Integer finalScore;

    // ========== 知识点表现 ==========
    @ApiModelProperty(value = "薄弱知识点列表（包括基本掌握的）")
    private List<KpointPerformanceVO> weakKpoints;

    @ApiModelProperty(value = "已掌握知识点列表")
    private List<KpointPerformanceVO> masteredKpoints;

    // ========== AI评语 ==========
    @ApiModelProperty(value = "AI生成的个性化评语")
    private String aiComment;

    // ========== 学习建议 ==========
    @ApiModelProperty(value = "学习建议列表")
    private List<String> suggestions;

    // ========== 历史对比 ==========
    @ApiModelProperty(value = "是否有历史记录")
    private Boolean hasHistory;

    @ApiModelProperty(value = "上次测评得分")
    private Integer lastScore;

    @ApiModelProperty(value = "得分变化")
    private Integer scoreChange;

    @ApiModelProperty(value = "上次测评时间")
    private LocalDateTime lastAssessedAt;

    @ApiModelProperty(value = "上次正确率（0.00-1.00）")
    private BigDecimal lastAccuracy;

    @ApiModelProperty(value = "正确率变化")
    private BigDecimal accuracyChange;

    @ApiModelProperty(value = "上次能力值（0.00-1.00）")
    private BigDecimal lastAbilityScore;

    @ApiModelProperty(value = "能力值变化")
    private BigDecimal abilityChange;

    @ApiModelProperty(value = "上次信心水平（0.00-1.00）")
    private BigDecimal lastConfidenceLevel;

    @ApiModelProperty(value = "信心水平变化")
    private BigDecimal confidenceChange;

    // ========== 排名信息 ==========
    @ApiModelProperty(value = "排名信息")
    private RankingInfoVO rankingInfo;

    // ========== 答题详情 ==========
    @ApiModelProperty(value = "答题详情列表")
    private List<AnswerDetailVO> answerDetails;
}
