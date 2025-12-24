package com.ruoyi.ailearn.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.ailearn.assessment.config.AssessmentConfig;
import com.ruoyi.ailearn.assessment.domain.AnswerDetail;
import com.ruoyi.ailearn.assessment.service.AdaptiveRecommendService;
import com.ruoyi.ailearn.assessment.util.AbilityResult;
import com.ruoyi.ailearn.course.domain.Kpoint;
import com.ruoyi.ailearn.course.domain.Question;
import com.ruoyi.ailearn.course.mapper.KpointMapper;
import com.ruoyi.ailearn.course.mapper.QuestionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自适应推荐服务实现类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 *
 * 艹，这是核心算法实现！6个阶段的自适应出题策略！
 * 题目数量可配置：知识点测评默认3-5题，章节测评默认6-10题
 */
@Slf4j
@Service
public class AdaptiveRecommendServiceImpl implements AdaptiveRecommendService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private KpointMapper kpointMapper;

    @Autowired
    private AssessmentConfig assessmentConfig;

    @Override
    public Question selectFirstQuestion(Integer assessmentType, Long chapterId, Long kpointId) {
        log.info("选择第1题 - 测评类型:{}, 章节ID:{}, 知识点ID:{}", assessmentType, chapterId, kpointId);

        // 目标难度：0.5（中等）
        // 难度范围：0.4 ~ 0.6
        // 区分度要求：>= 1.5（高区分度）

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 1. 知识点范围（艹，question表有两个知识点字段kpId和kpointId，必须同时匹配！）
        if (assessmentType == 2) {
            // 知识点测评：只从该知识点选题（兼容kpId和kpointId两个字段）
            wrapper.and(w -> w.eq(Question::getKpointId, kpointId)
                              .or()
                              .eq(Question::getKpId, kpointId));
        } else {
            // 章节测评：从章节下所有知识点选题
            List<Long> kpointIds = getKpointIdsByChapter(chapterId);
            if (kpointIds.isEmpty()) {
                throw new RuntimeException("该章节下没有知识点");
            }
            // 兼容kpId和kpointId两个字段
            wrapper.and(w -> w.in(Question::getKpointId, kpointIds)
                              .or()
                              .in(Question::getKpId, kpointIds));
        }

        // 2. 难度匹配（中等难度）
        wrapper.between(Question::getDifficulty, 0.4, 0.6);

        // 3. 高区分度优先
        wrapper.ge(Question::getDiscrimination, 1.5);

        // 4. 按质量分降序，取第一题
        wrapper.orderByDesc(Question::getQualityScore)
                .orderByDesc(Question::getDiscrimination)
                .last("LIMIT 1");

        Question question = questionMapper.selectOne(wrapper);

        if (question == null) {
            // 放宽条件再试
            log.warn("未找到符合条件的第1题，放宽条件重试");
            wrapper.clear();

            // 兼容kpId和kpointId两个字段
            if (assessmentType == 2) {
                wrapper.and(w -> w.eq(Question::getKpointId, kpointId)
                                  .or()
                                  .eq(Question::getKpId, kpointId));
            } else {
                List<Long> kpointIds = getKpointIdsByChapter(chapterId);
                wrapper.and(w -> w.in(Question::getKpointId, kpointIds)
                                  .or()
                                  .in(Question::getKpId, kpointIds));
            }

            wrapper.between(Question::getDifficulty, 0.3, 0.7)
                    .orderByDesc(Question::getQualityScore)
                    .last("LIMIT 1");

            question = questionMapper.selectOne(wrapper);
        }

        if (question == null) {
            throw new RuntimeException("题库中没有合适的题目");
        }

        log.info("选择第1题成功 - 题目ID:{}, 难度:{}", question.getId(), question.getDifficulty());
        return question;
    }

    @Override
    public Question recommendNextQuestion(Integer assessmentType,
                                          Long chapterId,
                                          Long kpointId,
                                          List<AnswerDetail> answerHistory,
                                          AbilityResult currentAbility) {

        int currentCount = answerHistory.size();
        log.info("推荐第{}题 - 当前能力值:{}, 信心水平:{}",
                currentCount + 1,
                currentAbility.getAbility(),
                currentAbility.getConfidence());

        // 计算目标难度
        double targetDifficulty = calculateTargetDifficulty(currentCount, answerHistory, currentAbility);

        log.info("计算目标难度 - 第{}题目标难度:{}", currentCount + 1, targetDifficulty);

        // 获取已答题目ID（去重）
        List<Long> answeredIds = answerHistory.stream()
                .map(AnswerDetail::getQuestionId)
                .collect(Collectors.toList());

        // 获取已考查知识点的次数（用于平衡）
        Map<Long, Long> kpointCountMap = answerHistory.stream()
                .collect(Collectors.groupingBy(
                        AnswerDetail::getKpointId,
                        Collectors.counting()
                ));

        // 获取知识点范围
        List<Long> targetKpointIds = getTargetKpointIds(assessmentType, chapterId, kpointId, kpointCountMap);

        // ========== 多级降级策略：逐步放宽条件直到找到题目 ==========

        Question selectedQuestion = null;

        // 第1次尝试：精确匹配（目标难度±0.1，高区分度≥1.5）
        selectedQuestion = tryFindQuestion(targetKpointIds, answeredIds, targetDifficulty, 0.1, 1.5, 5);
        if (selectedQuestion != null) {
            log.info("第1次尝试成功 - 精确匹配");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 第2次尝试：放宽难度范围（目标难度±0.2，高区分度≥1.5）
        selectedQuestion = tryFindQuestion(targetKpointIds, answeredIds, targetDifficulty, 0.2, 1.5, 10);
        if (selectedQuestion != null) {
            log.info("第2次尝试成功 - 放宽难度范围到±0.2");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 第3次尝试：降低区分度要求（目标难度±0.2，区分度≥1.2）
        selectedQuestion = tryFindQuestion(targetKpointIds, answeredIds, targetDifficulty, 0.2, 1.2, 10);
        if (selectedQuestion != null) {
            log.info("第3次尝试成功 - 降低区分度要求到≥1.2");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 第4次尝试：再次放宽难度范围（目标难度±0.3，区分度≥1.0）
        selectedQuestion = tryFindQuestion(targetKpointIds, answeredIds, targetDifficulty, 0.3, 1.0, 10);
        if (selectedQuestion != null) {
            log.info("第4次尝试成功 - 放宽难度范围到±0.3，区分度≥1.0");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 第5次尝试：选择最接近的难度值（不限制区分度）
        selectedQuestion = tryFindClosestDifficulty(targetKpointIds, answeredIds, targetDifficulty, 20);
        if (selectedQuestion != null) {
            log.info("第5次尝试成功 - 选择最接近的难度值");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 第6次尝试：任意符合知识点范围的未答题目（兜底策略）
        selectedQuestion = tryFindAnyQuestion(targetKpointIds, answeredIds, 20);
        if (selectedQuestion != null) {
            log.warn("第6次尝试成功 - 使用兜底策略：任意未答题目");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 第7次尝试：允许重复题目（极端情况）
        selectedQuestion = tryFindAnyQuestion(targetKpointIds, null, 20);
        if (selectedQuestion != null) {
            log.error("第7次尝试成功 - 极端情况：允许重复题目");
            logSelectedQuestion(currentCount, selectedQuestion);
            return selectedQuestion;
        }

        // 所有策略都失败，抛出异常
        throw new RuntimeException(String.format(
                "题库中没有更多合适的题目！目标难度:%.2f, 知识点范围:%s, 已答题数:%d",
                targetDifficulty, targetKpointIds, answeredIds.size()
        ));
    }

    /**
     * 获取目标知识点ID列表（章节测评需要平衡知识点）
     */
    private List<Long> getTargetKpointIds(Integer assessmentType,
                                          Long chapterId,
                                          Long kpointId,
                                          Map<Long, Long> kpointCountMap) {
        if (assessmentType == 2) {
            // 知识点测评：只从该知识点选题
            return Collections.singletonList(kpointId);
        } else {
            // 章节测评：从章节下所有知识点选题，优先选择未考查或考查次数少的
            List<Long> allKpointIds = getKpointIdsByChapter(chapterId);

            // 优先选择未考查或考查次数少的知识点（平衡性）
            List<Long> lessTestedKpoints = allKpointIds.stream()
                    .filter(kpId -> kpointCountMap.getOrDefault(kpId, 0L) < 2)
                    .collect(Collectors.toList());

            return lessTestedKpoints.isEmpty() ? allKpointIds : lessTestedKpoints;
        }
    }

    /**
     * 尝试根据难度范围和区分度查找题目
     *
     * @param kpointIds         知识点ID列表
     * @param answeredIds       已答题目ID列表
     * @param targetDifficulty  目标难度
     * @param diffRange         难度范围（±）
     * @param minDiscrimination 最低区分度
     * @param limit             候选题数量
     * @return 选中的题目，找不到返回null
     */
    private Question tryFindQuestion(List<Long> kpointIds,
                                     List<Long> answeredIds,
                                     double targetDifficulty,
                                     double diffRange,
                                     double minDiscrimination,
                                     int limit) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 知识点范围（兼容kpId和kpointId两个字段，这两个SB字段必须都匹配！）
        wrapper.and(w -> w.in(Question::getKpointId, kpointIds)
                          .or()
                          .in(Question::getKpId, kpointIds));

        // 难度范围
        double minDiff = Math.max(0.0, targetDifficulty - diffRange);
        double maxDiff = Math.min(1.0, targetDifficulty + diffRange);
        wrapper.between(Question::getDifficulty, minDiff, maxDiff);

        // 去重
        if (answeredIds != null && !answeredIds.isEmpty()) {
            wrapper.notIn(Question::getId, answeredIds);
        }

        // 区分度要求
        wrapper.ge(Question::getDiscrimination, minDiscrimination);

        // 排序：优先选择难度最接近的，其次区分度高的，最后质量分数高的
        wrapper.last(String.format("ORDER BY ABS(difficulty - %.2f) ASC, discrimination DESC, quality_score DESC LIMIT %d",
                targetDifficulty, limit));

        List<Question> candidates = questionMapper.selectList(wrapper);

        if (candidates.isEmpty()) {
            return null;
        }

        // 从候选题中随机选一道
        return candidates.get((int) (Math.random() * candidates.size()));
    }

    /**
     * 选择难度最接近的题目（不限制难度范围）
     */
    private Question tryFindClosestDifficulty(List<Long> kpointIds,
                                              List<Long> answeredIds,
                                              double targetDifficulty,
                                              int limit) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 知识点范围（兼容kpId和kpointId两个字段）
        wrapper.and(w -> w.in(Question::getKpointId, kpointIds)
                          .or()
                          .in(Question::getKpId, kpointIds));

        // 去重
        if (answeredIds != null && !answeredIds.isEmpty()) {
            wrapper.notIn(Question::getId, answeredIds);
        }

        // 排序：按难度接近程度排序，其次区分度
        wrapper.last(String.format("ORDER BY ABS(difficulty - %.2f) ASC, discrimination DESC LIMIT %d",
                targetDifficulty, limit));

        List<Question> candidates = questionMapper.selectList(wrapper);

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get((int) (Math.random() * candidates.size()));
    }

    /**
     * 查找任意符合知识点范围的题目（兜底策略）
     */
    private Question tryFindAnyQuestion(List<Long> kpointIds,
                                        List<Long> answeredIds,
                                        int limit) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 知识点范围（兼容kpId和kpointId两个字段）
        wrapper.and(w -> w.in(Question::getKpointId, kpointIds)
                          .or()
                          .in(Question::getKpId, kpointIds));

        // 去重（可选）
        if (answeredIds != null && !answeredIds.isEmpty()) {
            wrapper.notIn(Question::getId, answeredIds);
        }

        // 优先选择高质量题目
        wrapper.orderByDesc(Question::getQualityScore)
                .orderByDesc(Question::getDiscrimination)
                .last(String.format("LIMIT %d", limit));

        List<Question> candidates = questionMapper.selectList(wrapper);

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get((int) (Math.random() * candidates.size()));
    }

    /**
     * 记录选中题目的日志
     */
    private void logSelectedQuestion(int currentCount, Question question) {
        log.info("推荐第{}题成功 - 题目ID:{}, 难度:{}, 区分度:{}, 质量分:{}",
                currentCount + 1,
                question.getId(),
                question.getDifficulty(),
                question.getDiscrimination(),
                question.getQualityScore());
    }

    @Override
    public boolean canTerminate(Integer assessmentType,
                                int currentCount,
                                AbilityResult abilityResult,
                                List<AnswerDetail> history) {

        // 从配置类获取题数限制（艹，终于可配置了！）
        int minQuestions = assessmentConfig.getMinQuestions(assessmentType);
        int maxQuestions = assessmentConfig.getMaxQuestions(assessmentType);
        double confidenceThreshold = assessmentConfig.getTerminate().getConfidenceThreshold();
        double intervalWidthThreshold = assessmentConfig.getTerminate().getIntervalWidthThreshold();
        double basicConfidence = assessmentConfig.getTerminate().getBasicConfidenceThreshold();
        double basicIntervalWidth = assessmentConfig.getTerminate().getBasicIntervalWidthThreshold();

        String typeDesc = assessmentType == 2 ? "知识点测评" : "章节测评";
        log.debug("终止条件判断 - {}，当前题数:{}，配置范围:{}-{}", typeDesc, currentCount, minQuestions, maxQuestions);

        // 条件1：强制上限
        if (currentCount >= maxQuestions) {
            log.info("{}达到最大题数{}，终止测评", typeDesc, maxQuestions);
            return true;
        }

        // 条件2：最少题数未满足
        if (currentCount < minQuestions) {
            log.debug("{}未达到最少题数{}，继续测评", typeDesc, minQuestions);
            return false;
        }

        // 条件3：高信心 + 窄区间
        double confidence = abilityResult.getConfidence().doubleValue();
        double intervalWidth = abilityResult.getIntervalWidth().doubleValue();

        if (confidence >= confidenceThreshold && intervalWidth <= intervalWidthThreshold) {
            log.info("高信心({})且区间窄({})，终止测评", confidence, intervalWidth);
            return true;
        }

        // 条件4：基本满足要求
        if (currentCount >= minQuestions &&
                confidence >= basicConfidence &&
                intervalWidth <= basicIntervalWidth) {
            log.info("基本满足要求(信心:{}, 区间:{})，终止测评", confidence, intervalWidth);
            return true;
        }

        // 条件5：连续3题难度相近且答题表现一致（能力稳定）
        // 知识点测评至少需要3题，章节测评至少需要4题才检查这个条件
        int stableCheckThreshold = assessmentType == 2 ? 3 : 4;
        if (currentCount >= stableCheckThreshold && history.size() >= 3) {
            List<AnswerDetail> recent3 = history.subList(history.size() - 3, history.size());

            // 检查难度是否相近
            double minDiff = recent3.stream()
                    .mapToDouble(a -> a.getDifficulty().doubleValue())
                    .min().orElse(0.0);
            double maxDiff = recent3.stream()
                    .mapToDouble(a -> a.getDifficulty().doubleValue())
                    .max().orElse(1.0);

            // 检查答题是否一致
            long correctCount = recent3.stream()
                    .filter(AnswerDetail::getIsCorrect)
                    .count();

            boolean allCorrect = correctCount == 3;
            boolean allWrong = correctCount == 0;
            boolean diffSimilar = (maxDiff - minDiff) < 0.15;

            if (diffSimilar && (allCorrect || allWrong)) {
                log.info("连续3题难度相近且表现一致，终止测评");
                return true;
            }
        }

        log.debug("不满足终止条件，继续测评");
        return false;
    }

    /**
     * 计算目标难度（6个阶段策略）
     */
    private double calculateTargetDifficulty(int currentCount,
                                             List<AnswerDetail> history,
                                             AbilityResult ability) {

        double abilityValue = ability.getAbility().doubleValue();
        double lowerBound = ability.getLowerBound().doubleValue();
        double upperBound = ability.getUpperBound().doubleValue();
        double confidence = ability.getConfidence().doubleValue();

        // === 阶段1：第2题 - 快速试探 ===
        if (currentCount == 1) {
            AnswerDetail first = history.get(0);
            boolean correct = Boolean.TRUE.equals(first.getIsCorrect());
            double timeRatio = (double) first.getTimeSpent() / first.getStandardTime();

            if (correct && timeRatio < 1.0) {
                // 答对且快速 → 能力较高 → 大幅提升难度
                return 0.7;
            } else if (correct && timeRatio < 1.5) {
                // 答对但较慢 → 能力中上 → 小幅提升难度
                return 0.6;
            } else if (!correct && timeRatio > 1.5) {
                // 答错且慢 → 能力较低 → 大幅降低难度
                return 0.3;
            } else {
                // 答错但快 → 可能粗心 → 小幅降低难度
                return 0.4;
            }
        }

        // === 阶段2：第3题 - 继续快速试探 ===
        else if (currentCount == 2) {
            AnswerDetail second = history.get(1);
            boolean correct = Boolean.TRUE.equals(second.getIsCorrect());

            if (correct) {
                // 第2题答对，继续提升难度
                return Math.min(0.9, abilityValue + 0.15);
            } else {
                // 第2题答错，降低难度
                return Math.max(0.1, abilityValue - 0.15);
            }
        }

        // === 阶段3：第4-5题 - 精确定位 ===
        else if (currentCount <= 4) {
            // 在能力区间边界试探
            // 随机选择上界或下界附近（±0.05）
            boolean testUpper = Math.random() > 0.5;
            if (testUpper) {
                return Math.min(0.95, upperBound - 0.05);
            } else {
                return Math.max(0.05, lowerBound + 0.05);
            }
        }

        // === 阶段4：第6+题 - 验证确认 ===
        else {
            if (confidence >= 0.8) {
                // 信心高，验证边界
                boolean testUpper = Math.random() > 0.5;
                return testUpper ? upperBound : lowerBound;
            } else {
                // 信心不足，继续在能力值附近定位
                return abilityValue;
            }
        }
    }

    /**
     * 获取章节下所有知识点ID
     */
    private List<Long> getKpointIdsByChapter(Long chapterId) {
        List<Kpoint> kpoints = kpointMapper.selectList(
                new LambdaQueryWrapper<Kpoint>()
                        .eq(Kpoint::getChapterId, chapterId)
        );
        return kpoints.stream()
                .map(Kpoint::getKpointId)
                .collect(Collectors.toList());
    }
}
