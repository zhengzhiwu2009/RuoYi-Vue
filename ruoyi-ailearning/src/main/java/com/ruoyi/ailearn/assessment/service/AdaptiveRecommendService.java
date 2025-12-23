package com.ruoyi.ailearn.assessment.service;

import com.ruoyi.ailearn.assessment.domain.AnswerDetail;
import com.ruoyi.ailearn.assessment.util.AbilityResult;
import com.ruoyi.ailearn.course.domain.Question;

import java.util.List;

/**
 * 自适应推荐服务接口
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 *
 * 艹，这是核心算法！自适应出题的灵魂！
 */
public interface AdaptiveRecommendService {

    /**
     * 选择第1题（中等难度0.5）
     *
     * @param assessmentType 测评类型：1-章节测评，2-知识点测评
     * @param chapterId      章节ID（章节测评时传入）
     * @param kpointId       知识点ID（知识点测评时传入）
     * @return 第一题
     */
    Question selectFirstQuestion(Integer assessmentType, Long chapterId, Long kpointId);

    /**
     * 推荐下一题（核心算法！）
     *
     * @param assessmentType  测评类型
     * @param chapterId       章节ID
     * @param kpointId        知识点ID
     * @param answerHistory   答题历史
     * @param currentAbility  当前能力估计
     * @return 下一题
     */
    Question recommendNextQuestion(Integer assessmentType,
                                   Long chapterId,
                                   Long kpointId,
                                   List<AnswerDetail> answerHistory,
                                   AbilityResult currentAbility);

    /**
     * 判断是否可以终止测评
     *
     * @param currentCount   当前已答题数
     * @param abilityResult  能力计算结果
     * @param history        答题历史
     * @return true-可以终止，false-继续测评
     */
    boolean canTerminate(int currentCount,
                        AbilityResult abilityResult,
                        List<AnswerDetail> history);
}
