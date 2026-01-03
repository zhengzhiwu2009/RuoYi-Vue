package com.ruoyi.ailearn.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.ailearn.assessment.domain.AnswerDetail;
import com.ruoyi.ailearn.assessment.domain.AssessmentRecord;
import com.ruoyi.ailearn.assessment.domain.ChapterAssessmentKpoint;
import com.ruoyi.ailearn.assessment.domain.StudentKpointMastery;
import com.ruoyi.ailearn.assessment.mapper.AnswerDetailMapper;
import com.ruoyi.ailearn.assessment.mapper.AssessmentRecordMapper;
import com.ruoyi.ailearn.assessment.mapper.ChapterAssessmentKpointMapper;
import com.ruoyi.ailearn.assessment.mapper.StudentKpointMasteryMapper;
import com.ruoyi.ailearn.assessment.vo.*;
import com.ruoyi.ailearn.assessment.domain.*;
import com.ruoyi.ailearn.assessment.dto.StartAssessmentDTO;
import com.ruoyi.ailearn.assessment.dto.SubmitAnswerDTO;
import com.ruoyi.ailearn.assessment.mapper.*;
import com.ruoyi.ailearn.assessment.service.AdaptiveRecommendService;
import com.ruoyi.ailearn.assessment.service.AssessmentService;
import com.ruoyi.ailearn.assessment.util.AbilityResult;
import com.ruoyi.ailearn.assessment.util.AnswerJudgeUtil;
import com.ruoyi.ailearn.assessment.util.IRTAbilityCalculator;
import com.ruoyi.ailearn.assessment.vo.*;
import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.domain.Kpoint;
import com.ruoyi.ailearn.course.domain.Question;
import com.ruoyi.ailearn.course.mapper.ChapterMapper;
import com.ruoyi.ailearn.course.mapper.KpointMapper;
import com.ruoyi.ailearn.course.mapper.QuestionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测评服务实现类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 *
 * 艹，这是整个系统最核心的业务逻辑！
 */
@Slf4j
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRecordMapper assessmentRecordMapper;

    @Autowired
    private AnswerDetailMapper answerDetailMapper;

    @Autowired
    private StudentKpointMasteryMapper studentKpointMasteryMapper;

    @Autowired
    private ChapterAssessmentKpointMapper chapterAssessmentKpointMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private KpointMapper kpointMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private AdaptiveRecommendService adaptiveRecommendService;

    @Autowired
    private IRTAbilityCalculator irtAbilityCalculator;

    @Autowired
    private com.ruoyi.ailearn.assessment.config.AssessmentConfig assessmentConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionVO startAssessment(StartAssessmentDTO dto, Long userId) {
        log.info("开始测评 - 学生ID:{}, 测评类型:{}, 章节ID:{}, 知识点ID:{}",
            userId, dto.getAssessmentType(),
                dto.getChapterId(), dto.getKpointId());

        // 1. 创建测评记录
        AssessmentRecord record = new AssessmentRecord()
                .setStudentId(userId)
                .setCourseId(dto.getCourseId())
                .setAssessmentType(dto.getAssessmentType())
                .setChapterId(dto.getChapterId())
                .setKpointId(dto.getKpointId())
                .setTotalQuestions(0)
                .setCorrectCount(0)
                .setWrongCount(0)
                .setStartedAt(LocalDateTime.now())
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setStatus(0);  // 0-进行中

        assessmentRecordMapper.insert(record);

        log.info("测评记录创建成功 - 测评ID:{}", record.getId());

        // 2. 选择第1题（中等难度0.5）
        Question firstQuestion = adaptiveRecommendService.selectFirstQuestion(
                dto.getAssessmentType(),
                dto.getChapterId(),
                dto.getKpointId()
        );

        // 3. 转换为VO返回（传入测评类型，用于显示正确的题数范围）
        QuestionVO questionVO = convertToQuestionVO(firstQuestion, 1, record.getId(), dto.getAssessmentType());

        log.info("返回第1题 - 题目ID:{}, 难度:{}", firstQuestion.getId(), firstQuestion.getDifficulty());

        return questionVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnswerFeedbackVO submitAnswer(SubmitAnswerDTO dto) {
        log.info("提交答案 - 测评ID:{}, 题目ID:{}, 序号:{}, 答案:{}",
                dto.getAssessmentId(), dto.getQuestionId(),
                dto.getSequence(), dto.getStudentAnswer());

        // 1. 查询题目信息
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 2. 使用AnswerJudgeUtil判断答案（艹，这个工具能处理各种憨批格式问题！）
        AnswerJudgeUtil.JudgeResult judgeResult = AnswerJudgeUtil.judgeAnswer(
                question.getType(),
                dto.getStudentAnswer(),
                question.getAnswerItems()
        );

        // 3. 更新题目的答案
        question.setAnswerStr(judgeResult.getNormalizedCorrectAnswer());
        questionMapper.updateById( question);
        log.info("判断答案 - 题目ID:{}, 题型:{}, 学生答案:{}, 正确答案:{}, 判断结果:{}, 部分得分:{}, 正确数:{}/{}",
                dto.getQuestionId(), question.getType(),
                dto.getStudentAnswer(), question.getAnswerStr(),
                judgeResult.getFullyCorrect(), judgeResult.getItemScore(),
                judgeResult.getCorrectCount(), judgeResult.getTotalCount());

        // 3. 计算速度得分
        Integer standardTime = question.getAvgTime() != null ? question.getAvgTime() : 90;
        BigDecimal speedScore = calculateSpeedScore(dto.getTimeSpent(), standardTime);

        // 4. 获取知识点ID和名称（艹，question表的kpointName可能为空，必须从kpoint表获取！）
        Long actualKpointId = question.getKpointId() != null ? question.getKpointId() : question.getKpId();
        String kpointName = question.getKpointName();
        if (kpointName == null || kpointName.isEmpty()) {
            // question表中kpointName为空，从kpoint表获取
            if (actualKpointId != null) {
                Kpoint kpoint = kpointMapper.selectOne(
                        new LambdaQueryWrapper<Kpoint>()
                                .eq(Kpoint::getKpointId, actualKpointId)
                );
                if (kpoint != null) {
                    kpointName = kpoint.getName();
                    log.debug("从kpoint表获取知识点名称 - kpointId:{}, name:{}", actualKpointId, kpointName);
                }
            }
        }

        // 5. 保存答题详情（包含部分得分信息）
        AnswerDetail detail = new AnswerDetail()
                .setAssessmentId(dto.getAssessmentId())
                .setQuestionId(dto.getQuestionId())
                .setKpointId(actualKpointId)
                .setKpointName(kpointName)
                .setSequence(dto.getSequence())
                .setDifficulty(question.getDifficulty())
                .setDiscrimination(question.getDiscrimination())
                .setIsCorrect(judgeResult.getFullyCorrect())
                .setStudentAnswer(dto.getStudentAnswer())
                .setCorrectAnswer(question.getAnswerStr())
                .setTimeSpent(dto.getTimeSpent())
                .setStandardTime(standardTime)
                .setAnsweredAt(LocalDateTime.now())
                .setCreatedAt(LocalDateTime.now())
                .setSpeedScore(speedScore)
                .setItemScore(judgeResult.getItemScore());  // 部分得分（艹，这个很重要！）

        answerDetailMapper.insert(detail);

        log.info("答题详情保存成功 - 详情ID:{}", detail.getId());

        // 5. 查询所有答题历史
        List<AnswerDetail> history = answerDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerDetail>()
                        .eq(AnswerDetail::getAssessmentId, dto.getAssessmentId())
                        .orderByAsc(AnswerDetail::getSequence)
        );

        // 6. 计算能力值和能力区间（IRT算法）
        AbilityResult ability = irtAbilityCalculator.calculateAbility(history);

        log.info("IRT能力计算完成 - 能力值:{}, 信心水平:{}",
                ability.getAbility(), ability.getConfidence());

        // 7. 计算当前正确率
        long correctCount = history.stream()
                .filter(AnswerDetail::getIsCorrect)
                .count();
        BigDecimal currentAccuracy = BigDecimal.valueOf((double) correctCount / history.size())
                .setScale(4, RoundingMode.HALF_UP);

        // 8. 判断是否终止测评（根据测评类型使用不同的题数配置）
        AssessmentRecord record = assessmentRecordMapper.selectById(dto.getAssessmentId());
        boolean shouldFinish = adaptiveRecommendService.canTerminate(
                record.getAssessmentType(), history.size(), ability, history);

        log.info("判断终止条件 - 测评类型:{}, 已答题数:{}, 是否终止:{}",
                record.getAssessmentType() == 2 ? "知识点" : "章节", history.size(), shouldFinish);

        // 9. 构建响应
        AnswerFeedbackVO feedback = new AnswerFeedbackVO()
                .setIsCorrect(judgeResult.getFullyCorrect())
                .setCorrectAnswer(question.getAnswerStr())
                .setExplanation(question.getExplanation())
                .setCurrentAbility(ability.getAbility())
                .setCurrentAccuracy(currentAccuracy)
                .setAnsweredCount(history.size())
                .setIsFinished(shouldFinish);

        if (shouldFinish) {
            // 测评结束，生成报告
            log.info("测评结束，开始生成报告 - 测评ID:{}", dto.getAssessmentId());
            completeAssessment(dto.getAssessmentId(), history, ability);
            AssessmentReportVO report = getAssessmentReport(dto.getAssessmentId());
            feedback.setReport(report);
        } else {
            // 继续测评，推荐下一题
            log.info("继续测评，推荐下一题");
            Question nextQuestion = adaptiveRecommendService.recommendNextQuestion(
                    record.getAssessmentType(),
                    record.getChapterId(),
                    record.getKpointId(),
                    history,
                    ability
            );
            feedback.setNextQuestion(convertToQuestionVO(nextQuestion, history.size() + 1, dto.getAssessmentId(), record.getAssessmentType()));
        }

        return feedback;
    }

    @Override
    public AssessmentReportVO getAssessmentReport(Long assessmentId) {
        log.info("获取测评报告 - 测评ID:{}", assessmentId);

        // 1. 查询测评记录
        AssessmentRecord record = assessmentRecordMapper.selectById(assessmentId);
        if (record == null) {
            throw new RuntimeException("测评记录不存在");
        }

        if (record.getStatus() != 1) {
            throw new RuntimeException("测评未完成");
        }

        // 2. 查询答题详情
        List<AnswerDetail> answers = answerDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerDetail>()
                        .eq(AnswerDetail::getAssessmentId, assessmentId)
                        .orderByAsc(AnswerDetail::getSequence)
        );

        // 3. 构建报告
        AssessmentReportVO report = new AssessmentReportVO();
        report.setAssessmentId(assessmentId);
        report.setAssessmentType(record.getAssessmentType());
        report.setCompletedAt(record.getCompletedAt());

        // 设置章节或知识点名称
        if (record.getAssessmentType() == 1) {
            Chapter chapter = chapterMapper.selectById(record.getChapterId());
            report.setChapterName(chapter != null ? chapter.getName() : "");
        } else {
            Kpoint kpoint = kpointMapper.selectOne(
                    new LambdaQueryWrapper<Kpoint>()
                            .eq(Kpoint::getKpointId, record.getKpointId())
            );
            report.setKpointName(kpoint != null ? kpoint.getName() : "");
        }

        // 4. 测评概况
        report.setTotalQuestions(record.getTotalQuestions());
        report.setCorrectCount(record.getCorrectCount());
        report.setWrongCount(record.getWrongCount());
        report.setAccuracy(record.getAccuracy());
        report.setTotalTime(record.getTotalTime());
        report.setAvgTime(record.getAvgTimePerQuestion());

        // 5. 答题时间分析
        if (!answers.isEmpty()) {
            report.setMinTime(answers.stream()
                    .mapToInt(AnswerDetail::getTimeSpent)
                    .min().orElse(0));
            report.setMaxTime(answers.stream()
                    .mapToInt(AnswerDetail::getTimeSpent)
                    .max().orElse(0));
        }

        // 6. 掌握程度评定
        report.setAbilityScore(record.getAbilityScore());
        report.setConfidenceLevel(record.getConfidenceLevel());
        report.setMasteryLevel(record.getMasteryLevel());
        report.setMasteryLevelText(getMasteryLevelText(record.getMasteryLevel()));
        report.setFinalScore(record.getFinalScore());

        // 7. 知识点表现
        List<KpointPerformanceVO> weakKpoints = new ArrayList<>();
        List<KpointPerformanceVO> masteredKpoints = new ArrayList<>();

        if (record.getAssessmentType() == 1) {
            // 章节测评：从chapter_assessment_kpoint表查询
            List<ChapterAssessmentKpoint> kpointDetails = chapterAssessmentKpointMapper.selectList(
                    new LambdaQueryWrapper<ChapterAssessmentKpoint>()
                            .eq(ChapterAssessmentKpoint::getAssessmentId, assessmentId)
                            .orderByAsc(ChapterAssessmentKpoint::getAccuracy)
            );

            for (ChapterAssessmentKpoint detail : kpointDetails) {
                KpointPerformanceVO kp = new KpointPerformanceVO()
                        .setKpointId(detail.getKpointId())
                        .setKpointName(detail.getKpointName())
                        .setQuestionCount(detail.getQuestionCount())
                        .setCorrectCount(detail.getCorrectCount())
                        .setWrongCount(detail.getWrongCount())
                        .setAccuracy(detail.getAccuracy())
                        .setMasteryLevel(detail.getMasteryLevel())
                        .setMasteryLevelText(getMasteryLevelText(detail.getMasteryLevel()))
                        .setIsWeak(detail.getIsWeak());

                if ("proficient".equals(detail.getMasteryLevel())) {
                    // 掌握的知识点
                    masteredKpoints.add(kp);
                } else {
                    // 薄弱的知识点
                    weakKpoints.add(kp);
                }
            }
        } else {
            // 知识点测评：只有一个知识点
            KpointPerformanceVO kp = new KpointPerformanceVO()
                    .setKpointId(record.getKpointId())
                    .setKpointName(report.getKpointName())
                    .setQuestionCount(record.getTotalQuestions())
                    .setCorrectCount(record.getCorrectCount())
                    .setWrongCount(record.getWrongCount())
                    .setAccuracy(record.getAccuracy())
                    .setMasteryLevel(record.getMasteryLevel())
                    .setMasteryLevelText(getMasteryLevelText(record.getMasteryLevel()))
                    .setIsWeak("not_mastered".equals(record.getMasteryLevel()));

            if ("not_mastered".equals(record.getMasteryLevel())) {
                weakKpoints.add(kp);
            } else if ("proficient".equals(record.getMasteryLevel())) {
                masteredKpoints.add(kp);
            }
        }

        report.setWeakKpoints(weakKpoints);
        report.setMasteredKpoints(masteredKpoints);

        // 8. AI评语
        report.setAiComment(record.getAiComment());

        // 9. 学习建议
        List<String> suggestions = generateSuggestions(report, weakKpoints);
        report.setSuggestions(suggestions);

        // 10. 历史对比
        AssessmentRecord lastRecord = findLastAssessment(
                record.getStudentId(),
                record.getAssessmentType(),
                record.getChapterId(),
                record.getKpointId(),
                assessmentId
        );

        if (lastRecord != null) {
            report.setHasHistory(true);
            report.setLastScore(lastRecord.getFinalScore());
            report.setScoreChange(record.getFinalScore() - lastRecord.getFinalScore());
            report.setLastAssessedAt(lastRecord.getCompletedAt());
            // 新增历史对比字段
            report.setLastAccuracy(lastRecord.getAccuracy());
            report.setAccuracyChange(record.getAccuracy().subtract(lastRecord.getAccuracy()));
            report.setLastAbilityScore(lastRecord.getAbilityScore());
            report.setAbilityChange(record.getAbilityScore().subtract(lastRecord.getAbilityScore()));
            report.setLastConfidenceLevel(lastRecord.getConfidenceLevel());
            report.setConfidenceChange(record.getConfidenceLevel().subtract(lastRecord.getConfidenceLevel()));
        } else {
            report.setHasHistory(false);
        }

        // 11. 计算该单元所有用户平均测评用时（avgTimeStandard）
        Integer avgTimeStandard = calculateAvgTimeStandard(
                record.getAssessmentType(),
                record.getChapterId(),
                record.getKpointId()
        );
        report.setAvgTimeStandard(avgTimeStandard);

        // 12. 排名信息
        RankingInfoVO rankingInfo = calculateRankingInfo(
                record.getStudentId(),
                record.getAssessmentType(),
                record.getChapterId(),
                record.getKpointId(),
                record.getFinalScore()
        );
        report.setRankingInfo(rankingInfo);

        // 13. 答题详情
        List<AnswerDetailVO> answerDetailVOs = answers.stream()
                .map(this::convertToAnswerDetailVO)
                .collect(Collectors.toList());
        report.setAnswerDetails(answerDetailVOs);

        log.info("测评报告生成完成 - 测评ID:{}", assessmentId);

        return report;
    }

    @Override
    public List<AssessmentReportVO> getAssessmentHistory(Long studentId,
                                                          Long courseId,
                                                          Integer assessmentType,
                                                          Integer page,
                                                          Integer size) {
        log.info("查询测评历史 - 学生ID:{}, 课程ID:{}, 测评类型:{}, 页码:{}, 每页:{}",
                studentId, courseId, assessmentType, page, size);

        LambdaQueryWrapper<AssessmentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentRecord::getStudentId, studentId)
                .eq(AssessmentRecord::getStatus, 1);  // 只查已完成的

        if (courseId != null) {
            wrapper.eq(AssessmentRecord::getCourseId, courseId);
        }

        if (assessmentType != null) {
            wrapper.eq(AssessmentRecord::getAssessmentType, assessmentType);
        }

        wrapper.orderByDesc(AssessmentRecord::getCompletedAt)
                .last("LIMIT " + size + " OFFSET " + ((page - 1) * size));

        List<AssessmentRecord> records = assessmentRecordMapper.selectList(wrapper);

        return records.stream()
                .map(record -> getAssessmentReport(record.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentKpointMasteryVO> getKpointMastery(Long studentId,
                                                          Long courseId,
                                                          Long chapterId) {
        log.info("查询知识点掌握情况 - 学生ID:{}, 课程ID:{}, 章节ID:{}",
                studentId, courseId, chapterId);

        LambdaQueryWrapper<StudentKpointMastery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentKpointMastery::getStudentId, studentId)
                .eq(StudentKpointMastery::getCourseId, courseId);

        if (chapterId != null) {
            wrapper.eq(StudentKpointMastery::getChapterId, chapterId);
        }

        wrapper.orderByDesc(StudentKpointMastery::getLastAssessedAt);

        List<StudentKpointMastery> masteryList = studentKpointMasteryMapper.selectList(wrapper);

        return masteryList.stream()
                .map(this::convertToStudentKpointMasteryVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentKpointMasteryVO> getWeakKpoints(Long studentId,
                                                        Long courseId,
                                                        Integer limit) {
        log.info("查询薄弱知识点 - 学生ID:{}, 课程ID:{}, 返回数量:{}",
                studentId, courseId, limit);

        LambdaQueryWrapper<StudentKpointMastery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentKpointMastery::getStudentId, studentId)
                .eq(StudentKpointMastery::getIsWeak, true);

        if (courseId != null) {
            wrapper.eq(StudentKpointMastery::getCourseId, courseId);
        }

        wrapper.orderByAsc(StudentKpointMastery::getMasteryRate)
                .orderByDesc(StudentKpointMastery::getLastAssessedAt)
                .last("LIMIT " + limit);

        List<StudentKpointMastery> weakList = studentKpointMasteryMapper.selectList(wrapper);

        return weakList.stream()
                .map(this::convertToStudentKpointMasteryVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void abandonAssessment(Long assessmentId) {
        log.info("放弃测评 - 测评ID:{}", assessmentId);

        assessmentRecordMapper.update(null,
                new LambdaUpdateWrapper<AssessmentRecord>()
                        .set(AssessmentRecord::getStatus, 2)  // 2-已放弃
                        .eq(AssessmentRecord::getId, assessmentId)
        );

        log.info("测评已放弃 - 测评ID:{}", assessmentId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 完成测评（核心方法！）
     */
    private void completeAssessment(Long assessmentId,
                                     List<AnswerDetail> history,
                                     AbilityResult ability) {
        log.info("完成测评，生成报告 - 测评ID:{}", assessmentId);

        AssessmentRecord record = assessmentRecordMapper.selectById(assessmentId);

        // 1. 计算统计数据
        int totalQuestions = history.size();
        int correctCount = (int) history.stream()
                .filter(AnswerDetail::getIsCorrect)
                .count();
        int wrongCount = totalQuestions - correctCount;
        BigDecimal accuracy = BigDecimal.valueOf((double) correctCount / totalQuestions)
                .setScale(4, RoundingMode.HALF_UP);

        int totalTime = history.stream()
                .mapToInt(AnswerDetail::getTimeSpent)
                .sum();
        int avgTime = totalTime / totalQuestions;

        // 2. 判断掌握程度
        String masteryLevel = determineMasteryLevel(
                ability.getAbility().doubleValue(),
                accuracy.doubleValue(),
                history
        );

        // 3. 计算综合得分
        double avgTimeRatio = history.stream()
                .mapToDouble(a -> (double) a.getTimeSpent() / a.getStandardTime())
                .average().orElse(1.0);
        int finalScore = calculateFinalScore(
                ability.getAbility().doubleValue(),
                accuracy.doubleValue(),
                avgTimeRatio
        );

        // 4. 更新测评记录
        record.setTotalQuestions(totalQuestions)
                .setCorrectCount(correctCount)
                .setWrongCount(wrongCount)
                .setAccuracy(accuracy)
                .setAbilityScore(ability.getAbility())
                .setConfidenceLevel(ability.getConfidence())
                .setMasteryLevel(masteryLevel)
                .setFinalScore(finalScore)
                .setTotalTime(totalTime)
                .setAvgTimePerQuestion(avgTime)
                .setCompletedAt(LocalDateTime.now())
                .setStatus(1);  // 1-已完成

        assessmentRecordMapper.updateById(record);

        // 5. 章节测评：生成知识点明细
        if (record.getAssessmentType() == 1) {
            generateChapterAssessmentKpoints(assessmentId, record.getChapterId(), history);
        }

        // 6. 更新学生知识点掌握情况
        updateStudentKpointMastery(record, history);

        // 7. 异步调用AI生成评语（简化处理，可以后续优化）
        String aiComment = generateAIComment(record, history);
        record.setAiComment(aiComment);
        assessmentRecordMapper.updateById(record);

        log.info("测评完成 - 测评ID:{}, 得分:{}, 掌握程度:{}",
                assessmentId, finalScore, masteryLevel);
    }

    /**
     * 生成章节测评知识点明细
     */
    private void generateChapterAssessmentKpoints(Long assessmentId,
                                                   Long chapterId,
                                                   List<AnswerDetail> history) {
        log.info("生成章节测评知识点明细 - 测评ID:{}, 章节ID:{}", assessmentId, chapterId);

        // 按知识点分组统计
        Map<Long, List<AnswerDetail>> byKpoint = history.stream()
                .collect(Collectors.groupingBy(AnswerDetail::getKpointId));

        for (Map.Entry<Long, List<AnswerDetail>> entry : byKpoint.entrySet()) {
            Long kpointId = entry.getKey();
            List<AnswerDetail> kpAnswers = entry.getValue();

            int totalCount = kpAnswers.size();
            int correctCount = (int) kpAnswers.stream()
                    .filter(AnswerDetail::getIsCorrect)
                    .count();
            int wrongCount = totalCount - correctCount;
            BigDecimal accuracy = BigDecimal.valueOf((double) correctCount / totalCount)
                    .setScale(4, RoundingMode.HALF_UP);

            // 判断掌握程度
            String masteryLevel;
            boolean isWeak;
            if (accuracy.doubleValue() >= 0.80) {
                masteryLevel = "proficient";
                isWeak = false;
            } else if (accuracy.doubleValue() >= 0.50) {
                masteryLevel = "basic";
                isWeak = false;
            } else {
                masteryLevel = "not_mastered";
                isWeak = true;
            }

            // 获取知识点名称（优先从AnswerDetail获取，为空则从kpoint表获取）
            String kpointName = kpAnswers.get(0).getKpointName();
            if (kpointName == null || kpointName.isEmpty()) {
                Kpoint kpoint = kpointMapper.selectOne(
                        new LambdaQueryWrapper<Kpoint>()
                                .eq(Kpoint::getKpointId, kpointId)
                );
                if (kpoint != null) {
                    kpointName = kpoint.getName();
                }
            }

            // 插入记录
            ChapterAssessmentKpoint cak = new ChapterAssessmentKpoint()
                    .setAssessmentId(assessmentId)
                    .setChapterId(chapterId)
                    .setKpointId(kpointId)
                    .setKpointName(kpointName)
                    .setQuestionCount(totalCount)
                    .setCorrectCount(correctCount)
                    .setWrongCount(wrongCount)
                    .setAccuracy(accuracy)
                    .setMasteryLevel(masteryLevel)
                    .setCreatedAt(LocalDateTime.now())
                    .setIsWeak(isWeak);

            chapterAssessmentKpointMapper.insert(cak);
        }

        log.info("章节测评知识点明细生成完成 - 涉及{}个知识点", byKpoint.size());
    }

    /**
     * 更新学生知识点掌握情况（核心方法！）
     */
    private void updateStudentKpointMastery(AssessmentRecord assessment,
                                            List<AnswerDetail> answers) {
        log.info("更新学生知识点掌握情况 - 学生ID:{}, 测评ID:{}",
                assessment.getStudentId(), assessment.getId());

        // 按知识点分组
        Map<Long, List<AnswerDetail>> byKpoint = answers.stream()
                .collect(Collectors.groupingBy(AnswerDetail::getKpointId));

        for (Map.Entry<Long, List<AnswerDetail>> entry : byKpoint.entrySet()) {
            Long kpointId = entry.getKey();
            List<AnswerDetail> kpAnswers = entry.getValue();

            // 统计本次表现
            int currentTotal = kpAnswers.size();
            int currentCorrect = (int) kpAnswers.stream()
                    .filter(AnswerDetail::getIsCorrect)
                    .count();
            int currentWrong = currentTotal - currentCorrect;
            double currentAccuracy = (double) currentCorrect / currentTotal;

            // 查询现有记录
            StudentKpointMastery existing = studentKpointMasteryMapper.selectOne(
                    new LambdaQueryWrapper<StudentKpointMastery>()
                            .eq(StudentKpointMastery::getStudentId, assessment.getStudentId())
                            .eq(StudentKpointMastery::getKpointId, kpointId)
            );

            // 获取章节ID和知识点名称（直接从kpoint表获取，更可靠！）
            Kpoint kpoint = kpointMapper.selectOne(
                    new LambdaQueryWrapper<Kpoint>()
                            .eq(Kpoint::getKpointId, kpointId)
            );
            Long chapterId = kpoint != null ? kpoint.getChapterId() : null;
            // 知识点名称优先从kpoint表获取，fallback到AnswerDetail
            String kpointNameForMastery = kpoint != null ? kpoint.getName() : kpAnswers.get(0).getKpointName();

            if (existing == null) {
                // ========== 首次测评该知识点 ==========
                String masteryLevel = determineMasteryLevel(
                        assessment.getAbilityScore().doubleValue(),
                        currentAccuracy,
                        kpAnswers
                );
                boolean isWeak = "not_mastered".equals(masteryLevel);

                StudentKpointMastery newRecord = new StudentKpointMastery()
                        .setStudentId(assessment.getStudentId())
                        .setCourseId(assessment.getCourseId())
                        .setChapterId(chapterId)
                        .setKpointId(kpointId)
                        .setKpointName(kpointNameForMastery)
                        .setMasteryLevel(masteryLevel)
                        .setMasteryRate(BigDecimal.valueOf(currentAccuracy).setScale(4, RoundingMode.HALF_UP))
                        .setAbilityScore(assessment.getAbilityScore())
                        .setAssessmentCount(1)
                        .setTotalQuestions(currentTotal)
                        .setCorrectCount(currentCorrect)
                        .setWrongCount(currentWrong)
                        .setLastScore(assessment.getFinalScore())
                        .setHighestScore(assessment.getFinalScore())
                        .setFirstAssessedAt(LocalDateTime.now())
                        .setLastAssessedAt(LocalDateTime.now())
                        .setIsWeak(isWeak)
                        .setContinuousMasteryCount("proficient".equals(masteryLevel) ? 1 : 0)
                        .setCreatedAt(LocalDateTime.now())
                        .setUpdatedAt(LocalDateTime.now())
                        .setTrend("stable");

                studentKpointMasteryMapper.insert(newRecord);

                log.info("首次测评知识点 - 知识点ID:{}, 掌握程度:{}", kpointId, masteryLevel);

            } else {
                // ========== 已有历史记录，更新 ==========

                // 累加统计数据
                int newTotalQuestions = existing.getTotalQuestions() + currentTotal;
                int newCorrectCount = existing.getCorrectCount() + currentCorrect;
                int newWrongCount = existing.getWrongCount() + currentWrong;

                // 计算新的掌握率
                double newMasteryRate = (double) newCorrectCount / newTotalQuestions;

                // 判断新的掌握程度
                String newMasteryLevel = determineMasteryLevel(
                        assessment.getAbilityScore().doubleValue(),
                        newMasteryRate,
                        answers
                );
                boolean isWeak = "not_mastered".equals(newMasteryLevel);

                // 更新连续掌握次数
                int continuousMasteryCount;
                if ("proficient".equals(newMasteryLevel)) {
                    continuousMasteryCount = existing.getContinuousMasteryCount() + 1;
                } else {
                    continuousMasteryCount = 0;  // 重置
                }

                // 计算进步趋势
                String trend;
                int scoreChange = assessment.getFinalScore() - existing.getLastScore();
                if (scoreChange > 5) {
                    trend = "improving";    // 进步
                } else if (scoreChange < -5) {
                    trend = "declining";    // 退步
                } else {
                    trend = "stable";       // 稳定
                }

                // 更新记录
                existing.setMasteryLevel(newMasteryLevel)
                        .setMasteryRate(BigDecimal.valueOf(newMasteryRate).setScale(4, RoundingMode.HALF_UP))
                        .setAbilityScore(assessment.getAbilityScore())
                        .setAssessmentCount(existing.getAssessmentCount() + 1)
                        .setTotalQuestions(newTotalQuestions)
                        .setCorrectCount(newCorrectCount)
                        .setWrongCount(newWrongCount)
                        .setLastScore(assessment.getFinalScore())
                        .setHighestScore(Math.max(existing.getHighestScore(), assessment.getFinalScore()))
                        .setLastAssessedAt(LocalDateTime.now())
                        .setIsWeak(isWeak)
                        .setContinuousMasteryCount(continuousMasteryCount)
                        .setUpdatedAt(LocalDateTime.now())
                        .setTrend(trend);

                studentKpointMasteryMapper.updateById(existing);

                log.info("更新知识点掌握情况 - 知识点ID:{}, 掌握程度:{}, 趋势:{}",
                        kpointId, newMasteryLevel, trend);
            }
        }

        log.info("学生知识点掌握情况更新完成 - 涉及{}个知识点", byKpoint.size());
    }

    /**
     * 生成AI评语（简化版）
     * TODO: 后续可以集成真实的AI服务
     */
    private String generateAIComment(AssessmentRecord record, List<AnswerDetail> answers) {
        // 简化处理，返回默认评语
        String masteryText = getMasteryLevelText(record.getMasteryLevel());

        StringBuilder comment = new StringBuilder();
        comment.append("你好！本次测评已完成，你的表现是：").append(masteryText).append("。\n\n");

        if ("proficient".equals(record.getMasteryLevel())) {
            comment.append("非常棒！你对这部分知识掌握得很扎实，继续保持！\n");
            comment.append("建议：可以尝试一些更有挑战性的题目，进一步提升能力。");
        } else if ("basic".equals(record.getMasteryLevel())) {
            comment.append("不错！基础知识掌握得比较好，但还有提升空间。\n");
            comment.append("建议：多做一些综合应用题，加强知识点之间的联系和迁移。");
        } else {
            comment.append("加油！这部分知识还需要继续努力。\n");
            comment.append("建议：先复习课本的重点内容，把基础打牢，再做练习题巩固。");
        }

        return comment.toString();
    }

    /**
     * 计算速度得分
     */
    private BigDecimal calculateSpeedScore(int timeSpent, int standardTime) {
        double timeRatio = (double) timeSpent / standardTime;
        double speedScore;

        if (timeRatio <= 0.5) {
            // 太快，可能粗心
            speedScore = 0.7;
        } else if (timeRatio <= 1.0) {
            // 正常速度
            speedScore = 1.0;
        } else if (timeRatio <= 1.5) {
            // 稍慢
            speedScore = 1.0 - (timeRatio - 1.0);  // 0.5~1.0
        } else {
            // 太慢
            speedScore = 0.3;
        }

        return BigDecimal.valueOf(speedScore).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * 判断掌握程度
     */
    private String determineMasteryLevel(double abilityScore,
                                         double accuracy,
                                         List<AnswerDetail> answers) {
        if (abilityScore >= 0.70 && accuracy >= 0.80) {
            // 检查是否答对了高难度题
            long hardCorrect = answers.stream()
                    .filter(a -> a.getDifficulty().doubleValue() > 0.7)
                    .filter(AnswerDetail::getIsCorrect)
                    .count();

            if (hardCorrect > 0) {
                return "proficient";  // 熟练掌握
            } else {
                return "basic";  // 基本掌握
            }
        } else if (abilityScore >= 0.40 && accuracy >= 0.50) {
            return "basic";  // 基本掌握
        } else {
            return "not_mastered";  // 未掌握
        }
    }

    /**
     * 计算综合得分（0-100）
     */
    private int calculateFinalScore(double abilityScore,
                                     double accuracy,
                                     double avgTimeRatio) {
        // 速度加成：比标准时间快有加分
        double speedBonus = Math.max(0, (1.5 - avgTimeRatio)) * 10;

        // 综合得分
        double score = 0.6 * abilityScore * 100 +   // 能力值60%
                0.3 * accuracy * 100 +                // 正确率30%
                0.1 * speedBonus;                     // 速度加成10%

        return (int) Math.min(100, Math.max(0, score));
    }

    /**
     * 生成学习建议
     */
    private List<String> generateSuggestions(AssessmentReportVO report,
                                              List<KpointPerformanceVO> weakKpoints) {
        List<String> suggestions = new ArrayList<>();

        if (!weakKpoints.isEmpty()) {
            for (KpointPerformanceVO weak : weakKpoints) {
                suggestions.add("重点复习【" + weak.getKpointName() + "】，建议完成5-10道相关练习题");
            }
        }

        if (report.getAvgTime() != null && report.getAvgTime() > 120) {
            suggestions.add("答题速度偏慢，建议多做限时练习，提高解题效率");
        }

        if ("basic".equals(report.getMasteryLevel())) {
            suggestions.add("基础知识掌握较好，建议多做一些综合应用题，提升能力");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("继续保持良好的学习状态，定期复习巩固");
        }

        return suggestions;
    }

    /**
     * 查找上一次测评记录
     */
    private AssessmentRecord findLastAssessment(Long studentId,
                                                Integer assessmentType,
                                                Long chapterId,
                                                Long kpointId,
                                                Long currentAssessmentId) {
        LambdaQueryWrapper<AssessmentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentRecord::getStudentId, studentId)
                .eq(AssessmentRecord::getAssessmentType, assessmentType)
                .eq(AssessmentRecord::getStatus, 1)  // 已完成
                .ne(AssessmentRecord::getId, currentAssessmentId);  // 排除当前测评

        if (assessmentType == 1 && chapterId != null) {
            wrapper.eq(AssessmentRecord::getChapterId, chapterId);
        } else if (assessmentType == 2 && kpointId != null) {
            wrapper.eq(AssessmentRecord::getKpointId, kpointId);
        }

        wrapper.orderByDesc(AssessmentRecord::getCompletedAt)
                .last("LIMIT 1");

        return assessmentRecordMapper.selectOne(wrapper);
    }

    /**
     * 获取掌握程度文本
     */
    private String getMasteryLevelText(String masteryLevel) {
        if ("proficient".equals(masteryLevel)) {
            return "熟练掌握";
        } else if ("basic".equals(masteryLevel)) {
            return "基本掌握";
        } else if ("not_mastered".equals(masteryLevel)) {
            return "未掌握";
        }
        return "未知";
    }

    /**
     * 转换为QuestionVO（艹，必须从kpoint表获取知识点名称，question表可能为空！）
     *
     * @param question       题目
     * @param sequence       题目序号
     * @param assessmentId   测评ID
     * @param assessmentType 测评类型：1-章节测评，2-知识点测评
     */
    private QuestionVO convertToQuestionVO(Question question, int sequence, Long assessmentId, Integer assessmentType) {
        QuestionVO vo = new QuestionVO();
        vo.setQuestionId(question.getId());
        vo.setSequence(sequence);
        vo.setProblem(question.getProblem());
        vo.setType(question.getType());
        vo.setOptions(question.getOptions());
        vo.setDifficulty(question.getDifficulty());
        vo.setStandardTime(question.getAvgTime() != null ? question.getAvgTime() : 90);
        vo.setAnswerItems(question.getAnswerItems());

        // 根据测评类型获取题数范围（艹，终于可配置了！）
        int minQuestions = assessmentConfig.getMinQuestions(assessmentType);
        int maxQuestions = assessmentConfig.getMaxQuestions(assessmentType);
        vo.setProgress("第" + sequence + "题/共" + minQuestions + "-" + maxQuestions + "题");

        // 获取知识点ID和名称（兼容kpId和kpointId两个字段）
        Long actualKpointId = question.getKpointId() != null ? question.getKpointId() : question.getKpId();
        String kpointName = question.getKpointName();
        if (kpointName == null || kpointName.isEmpty()) {
            // question表中kpointName为空，从kpoint表获取
            if (actualKpointId != null) {
                Kpoint kpoint = kpointMapper.selectOne(
                        new LambdaQueryWrapper<Kpoint>()
                                .eq(Kpoint::getKpointId, actualKpointId)
                );
                if (kpoint != null) {
                    kpointName = kpoint.getName();
                }
            }
        }
        vo.setKpointId(actualKpointId);
        vo.setKpointName(kpointName);
        vo.setAssessmentId(assessmentId);
        return vo;
    }

    /**
     * 转换为AnswerDetailVO
     */
    private AnswerDetailVO convertToAnswerDetailVO(AnswerDetail detail) {
        AnswerDetailVO vo = new AnswerDetailVO();
        BeanUtils.copyProperties(detail, vo);

        // 获取题目内容和解析
        Question question = questionMapper.selectById(detail.getQuestionId());
        if (question != null) {
            vo.setProblem(question.getProblem());
            vo.setExplanation(question.getExplanation());
        }

        return vo;
    }

    /**
     * 转换为StudentKpointMasteryVO
     */
    private StudentKpointMasteryVO convertToStudentKpointMasteryVO(StudentKpointMastery mastery) {
        StudentKpointMasteryVO vo = new StudentKpointMasteryVO();
        BeanUtils.copyProperties(mastery, vo);
        vo.setMasteryLevelText(getMasteryLevelText(mastery.getMasteryLevel()));
        vo.setTrendText(getTrendText(mastery.getTrend()));

        // 获取章节名称
        if (mastery.getChapterId() != null) {
            Chapter chapter = chapterMapper.selectById(mastery.getChapterId());
            if (chapter != null) {
                vo.setChapterName(chapter.getName());
            }
        }

        return vo;
    }

    /**
     * 获取趋势文本
     */
    private String getTrendText(String trend) {
        if ("improving".equals(trend)) {
            return "进步中";
        } else if ("stable".equals(trend)) {
            return "稳定";
        } else if ("declining".equals(trend)) {
            return "退步";
        }
        return "未知";
    }

    /**
     * 计算该单元所有用户平均测评总用时
     * 艹，这个方法用来计算同一单元所有用户的平均总用时，用于对比参考！
     * 注意：是总用时的平均，不是单题用时的平均！
     */
    private Integer calculateAvgTimeStandard(Integer assessmentType, Long chapterId, Long kpointId) {
        LambdaQueryWrapper<AssessmentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentRecord::getAssessmentType, assessmentType)
                .eq(AssessmentRecord::getStatus, 1);  // 只统计已完成的

        if (assessmentType == 1 && chapterId != null) {
            wrapper.eq(AssessmentRecord::getChapterId, chapterId);
        } else if (assessmentType == 2 && kpointId != null) {
            wrapper.eq(AssessmentRecord::getKpointId, kpointId);
        }

        List<AssessmentRecord> records = assessmentRecordMapper.selectList(wrapper);

        if (records.isEmpty()) {
            return null;  // 没有数据时返回null
        }

        // 计算平均总用时（不是单题用时！）
        double avgTotalTime = records.stream()
                .filter(r -> r.getTotalTime() != null && r.getTotalTime() > 0)
                .mapToInt(AssessmentRecord::getTotalTime)
                .average()
                .orElse(0.0);

        return avgTotalTime > 0 ? (int) Math.round(avgTotalTime) : null;
    }

    /**
     * 计算排名信息
     * 艹，这个方法用来计算学生在同年级和系统范围内的排名！
     * 同年级定义：同一学年（9月到次年6月）
     */
    private RankingInfoVO calculateRankingInfo(Long studentId, Integer assessmentType,
                                                Long chapterId, Long kpointId, Integer score) {
        RankingInfoVO rankingInfo = new RankingInfoVO();

        // 构建查询条件
        LambdaQueryWrapper<AssessmentRecord> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(AssessmentRecord::getAssessmentType, assessmentType)
                .eq(AssessmentRecord::getStatus, 1);

        if (assessmentType == 1 && chapterId != null) {
            baseWrapper.eq(AssessmentRecord::getChapterId, chapterId);
        } else if (assessmentType == 2 && kpointId != null) {
            baseWrapper.eq(AssessmentRecord::getKpointId, kpointId);
        }

        // 1. 系统总排名（按该单元所有用户最高分排名）
        List<AssessmentRecord> allRecords = assessmentRecordMapper.selectList(baseWrapper);

        // 按学生分组，取每个学生的最高分
        Map<Long, Integer> studentMaxScores = allRecords.stream()
                .collect(Collectors.groupingBy(
                        AssessmentRecord::getStudentId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(AssessmentRecord::getFinalScore)),
                                opt -> opt.map(AssessmentRecord::getFinalScore).orElse(0)
                        )
                ));

        int systemTotal = studentMaxScores.size();
        int higherThanMe = (int) studentMaxScores.values().stream()
                .filter(s -> s > score)
                .count();
        int systemRank = higherThanMe + 1;

        rankingInfo.setSystemTotal(systemTotal);
        rankingInfo.setSystemRank(systemRank);
        rankingInfo.setSystemBeatPercent(systemTotal > 1 ?
                BigDecimal.valueOf((double) (systemTotal - systemRank) / (systemTotal - 1))
                        .setScale(4, RoundingMode.HALF_UP) :
                BigDecimal.ONE);

        // 2. 同年级排名（按学年：9月到次年6月）
        // 简化处理：目前按同一学年内的记录计算
        // TODO: 后续可以结合学生年级信息进行更精确的计算
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // 确定学年范围
        LocalDateTime startOfYear;
        LocalDateTime endOfYear;
        if (currentMonth >= 9) {
            // 当前是9月之后，学年是 currentYear.9 ~ (currentYear+1).6
            startOfYear = LocalDateTime.of(currentYear, 9, 1, 0, 0, 0);
            endOfYear = LocalDateTime.of(currentYear + 1, 6, 30, 23, 59, 59);
        } else {
            // 当前是1-8月，学年是 (currentYear-1).9 ~ currentYear.6
            startOfYear = LocalDateTime.of(currentYear - 1, 9, 1, 0, 0, 0);
            endOfYear = LocalDateTime.of(currentYear, 6, 30, 23, 59, 59);
        }

        // 查询本学年的记录
        LambdaQueryWrapper<AssessmentRecord> classWrapper = baseWrapper.clone();
        classWrapper.ge(AssessmentRecord::getCompletedAt, startOfYear)
                .le(AssessmentRecord::getCompletedAt, endOfYear);

        List<AssessmentRecord> classRecords = assessmentRecordMapper.selectList(classWrapper);

        // 按学生分组，取每个学生的最高分
        Map<Long, Integer> classStudentMaxScores = classRecords.stream()
                .collect(Collectors.groupingBy(
                        AssessmentRecord::getStudentId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(AssessmentRecord::getFinalScore)),
                                opt -> opt.map(AssessmentRecord::getFinalScore).orElse(0)
                        )
                ));

        int classTotal = classStudentMaxScores.size();
        int classHigherThanMe = (int) classStudentMaxScores.values().stream()
                .filter(s -> s > score)
                .count();
        int classRank = classHigherThanMe + 1;

        rankingInfo.setClassTotal(classTotal);
        rankingInfo.setClassRank(classRank);
        rankingInfo.setClassBeatPercent(classTotal > 1 ?
                BigDecimal.valueOf((double) (classTotal - classRank) / (classTotal - 1))
                        .setScale(4, RoundingMode.HALF_UP) :
                BigDecimal.ONE);

        log.info("排名计算完成 - 学生ID:{}, 系统排名:{}/{}, 同年级排名:{}/{}",
                studentId, systemRank, systemTotal, classRank, classTotal);

        return rankingInfo;
    }

    @Override
    public List<HistoryTrendVO> getHistoryTrend(Long studentId, Long chapterId, Integer limit) {
        log.info("获取历史趋势数据 - 学生ID:{}, 章节ID:{}, 限制:{}", studentId, chapterId, limit);

        if (limit == null || limit <= 0) {
            limit = 6;  // 默认返回6条
        }

        LambdaQueryWrapper<AssessmentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentRecord::getStudentId, studentId)
                .eq(AssessmentRecord::getAssessmentType, 1)  // 章节测评
                .eq(AssessmentRecord::getChapterId, chapterId)
                .eq(AssessmentRecord::getStatus, 1)  // 已完成
                .orderByDesc(AssessmentRecord::getCompletedAt)
                .last("LIMIT " + limit);

        List<AssessmentRecord> records = assessmentRecordMapper.selectList(wrapper);

        // 倒序排列，让时间线从早到晚
        Collections.reverse(records);

        List<HistoryTrendVO> trends = records.stream()
                .map(record -> new HistoryTrendVO()
                        .setDate(record.getCompletedAt())
                        .setScore(record.getFinalScore())
                        .setAbility(record.getAbilityScore())
                        .setAccuracy(record.getAccuracy())
                        .setAssessmentId(record.getId()))
                .collect(Collectors.toList());

        log.info("历史趋势数据获取完成 - 返回{}条记录", trends.size());

        return trends;
    }
}
