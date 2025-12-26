package com.ruoyi.ailearn.assessment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 测评配置类（支持application.yml配置）
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-23
 *
 * 艹，终于把这些魔法数字搞成可配置的了！
 */
@Data
@Component
@ConfigurationProperties(prefix = "assessment")
public class AssessmentConfig {

    /**
     * 知识点测评配置
     */
    private KpointAssessment kpoint = new KpointAssessment();

    /**
     * 章节测评配置
     */
    private ChapterAssessment chapter = new ChapterAssessment();

    /**
     * 终止条件配置
     */
    private TerminateCondition terminate = new TerminateCondition();

    /**
     * 知识点测评配置
     */
    @Data
    public static class KpointAssessment {
        /**
         * 最少题数（默认3道）
         */
        private int minQuestions = 3;

        /**
         * 最多题数（默认5道）
         */
        private int maxQuestions = 5;
    }

    /**
     * 章节测评配置
     */
    @Data
    public static class ChapterAssessment {
        /**
         * 最少题数（默认6道）
         */
        private int minQuestions = 6;

        /**
         * 最多题数（默认10道）
         */
        private int maxQuestions = 10;
    }

    /**
     * 终止条件配置
     */
    @Data
    public static class TerminateCondition {
        /**
         * 信心水平阈值（默认0.85）
         */
        private double confidenceThreshold = 0.85;

        /**
         * 区间宽度阈值（默认0.2）
         */
        private double intervalWidthThreshold = 0.2;

        /**
         * 基本满足条件的信心水平（默认0.75）
         */
        private double basicConfidenceThreshold = 0.75;

        /**
         * 基本满足条件的区间宽度（默认0.25）
         */
        private double basicIntervalWidthThreshold = 0.25;
    }

    // ==================== 便捷方法 ====================

    /**
     * 根据测评类型获取最少题数
     *
     * @param assessmentType 测评类型：1-章节，2-知识点
     * @return 最少题数
     */
    public int getMinQuestions(Integer assessmentType) {
        return assessmentType == 2 ? kpoint.getMinQuestions() : chapter.getMinQuestions();
    }

    /**
     * 根据测评类型获取最多题数
     *
     * @param assessmentType 测评类型：1-章节，2-知识点
     * @return 最多题数
     */
    public int getMaxQuestions(Integer assessmentType) {
        return assessmentType == 2 ? kpoint.getMaxQuestions() : chapter.getMaxQuestions();
    }
}
