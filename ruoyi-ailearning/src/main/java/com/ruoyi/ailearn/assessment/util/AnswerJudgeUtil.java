package com.ruoyi.ailearn.assessment.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 答案判断工具类
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-19
 *
 * 艹！这个工具专门处理各种憨批的答案格式问题
 * 支持：
 * 1. 选择题(type=0): 整体比较，忽略空格和大小写
 * 2. 填空题(type=1): 分号分隔多个填空，支持部分得分
 */
@Slf4j
public class AnswerJudgeUtil {

    /**
     * 数学公式标签的正则（艹，这个SB标签得用正则扒内容）
     * 匹配 <span class="math">xxx</span> 中的 xxx
     */
    private static final Pattern MATH_SPAN_PATTERN = Pattern.compile(
            "<span\\s+class\\s*=\\s*[\"']math[\"']\\s*>(.*?)</span>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * 从JSON格式的正确答案中提取答案内容
     *
     * 输入格式示例：
     * [{"isCorrect":false,"answer":"<span class=\"math\">104999</span>","userAnswer":"C","hasMathFormular":true},
     *  {"isCorrect":false,"answer":"<span class=\"math\">95000</span>","userAnswer":"","hasMathFormular":true}]
     *
     * 输出：104999;95000
     *
     * @param jsonAnswer JSON格式的答案字符串
     * @return 提取后的答案，多个用分号分隔
     */
    public static String extractAnswerFromJson(String jsonAnswer) {
        if (jsonAnswer == null || jsonAnswer.trim().isEmpty()) {
            log.warn("JSON答案为空，这是要闹哪样？");
            return "";
        }

        try {
            JSONArray answerArray = JSON.parseArray(jsonAnswer);
            if (answerArray == null || answerArray.isEmpty()) {
                log.warn("JSON答案解析为空数组 - input:{}", jsonAnswer);
                return "";
            }

            List<String> answers = new ArrayList<>();

            for (int i = 0; i < answerArray.size(); i++) {
                JSONObject item = answerArray.getJSONObject(i);
                String answer = item.getString("answer");
                Boolean hasMathFormular = item.getBoolean("hasMathFormular");

                if (answer == null) {
                    log.warn("第{}个答案项的answer属性为空，这SB数据是怎么来的？", i + 1);
                    answers.add("");
                    continue;
                }

                // 如果是数学公式，需要从<span class="math">xxx</span>中提取内容
                if (Boolean.TRUE.equals(hasMathFormular)) {
                    String extractedAnswer = extractMathContent(answer);
                    answers.add(extractedAnswer);
                    log.debug("数学公式答案提取 - 原始:{} → 提取:{}", answer, extractedAnswer);
                } else {
                    // 非数学公式，直接使用原答案（去除HTML标签）
                    String cleanAnswer = stripHtmlTags(answer);
                    answers.add(cleanAnswer);
                    log.debug("普通答案提取 - 原始:{} → 清理:{}", answer, cleanAnswer);
                }
            }

            String result = String.join(";", answers);
            log.info("JSON答案提取完成 - 共{}个答案 → {}", answers.size(), result);
            return result;

        } catch (Exception e) {
            log.error("艹！JSON答案解析失败 - input:{}, error:{}", jsonAnswer, e.getMessage(), e);
            return "";
        }
    }

    /**
     * 从数学公式标签中提取内容
     * 例如：<span class="math">104999</span> → 104999
     */
    private static String extractMathContent(String input) {
        if (input == null) {
            return "";
        }

        Matcher matcher = MATH_SPAN_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(matcher.group(1).trim());
        }

        // 如果没匹配到数学公式标签，返回去除HTML后的原文
        if (result.length() == 0) {
            log.debug("未匹配到数学公式标签，直接清理HTML - input:{}", input);
            return stripHtmlTags(input);
        }

        return result.toString();
    }

    /**
     * 去除HTML标签（保底方案，防止憨批数据）
     */
    private static String stripHtmlTags(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * 判断答案是否正确
     *
     * @param questionType   题目类型：0-选择题，1-填空题
     * @param studentAnswer  学生答案
     * @param correctAnswer  正确答案
     * @return 判断结果
     */
    public static JudgeResult judgeAnswer(Integer questionType, String studentAnswer, String correctAnswer) {
        // 基本校验（这些SB情况必须处理）
        if (studentAnswer == null || correctAnswer == null) {
            log.warn("答案为空 - 学生答案:{}, 正确答案:{}", studentAnswer, correctAnswer);
            return JudgeResult.allWrong();
        }
        // 从JSON中提取答案
        correctAnswer = extractAnswerFromJson(correctAnswer);

        // 根据题型判断
        if (questionType == null || questionType == 0) {
            // 选择题：整体比较
            return judgeChoiceQuestion(studentAnswer, correctAnswer);
        } else if (questionType == 1) {
            // 填空题：分号分隔，支持部分得分
            return judgeFillBlankQuestion(studentAnswer, correctAnswer);
        } else {
            log.warn("未知题型 - type:{}", questionType);
            return judgeChoiceQuestion(studentAnswer, correctAnswer);
        }
    }

    /**
     * 判断选择题（type=0）
     * 策略：去除所有空格后，大小写不敏感比较
     */
    private static JudgeResult judgeChoiceQuestion(String studentAnswer, String correctAnswer) {
        // 标准化答案：去除所有空格，转小写
        String normalizedStudent = normalizeText(studentAnswer);
        String normalizedCorrect = normalizeText(correctAnswer);

        boolean isCorrect = normalizedStudent.equals(normalizedCorrect);

        log.debug("选择题判断 - 学生:{} → {}, 正确:{} → {}, 结果:{}",
                studentAnswer, normalizedStudent,
                correctAnswer, normalizedCorrect,
                isCorrect);

        return new JudgeResult()
                .setFullyCorrect(isCorrect)
                .setItemScore(isCorrect ? BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP))
                .setCorrectCount(isCorrect ? 1 : 0)
                .setTotalCount(1)
                .setNormalizedStudentAnswer(normalizedStudent)
                .setNormalizedCorrectAnswer(normalizedCorrect);
    }

    /**
     * 判断填空题（type=1）
     * 策略：分号分隔多个填空，每个填空去空格后比较，支持部分得分
     */
    private static JudgeResult judgeFillBlankQuestion(String studentAnswer, String correctAnswer) {
        // 按分号分隔
        String[] studentBlanks = studentAnswer.split(";");
        String[] correctBlanks = correctAnswer.split(";");

        // 填空数量必须一致（这是基本要求，憨批！）
        if (studentBlanks.length != correctBlanks.length) {
            log.warn("填空数量不一致 - 学生:{}个, 正确:{}个",
                    studentBlanks.length, correctBlanks.length);
            return JudgeResult.allWrong()
                    .setTotalCount(correctBlanks.length);
        }

        int totalCount = correctBlanks.length;
        int correctCount = 0;
        List<String> normalizedStudentList = new ArrayList<>();
        List<String> normalizedCorrectList = new ArrayList<>();

        // 逐个填空比较
        for (int i = 0; i < totalCount; i++) {
            String studentBlank = normalizeText(studentBlanks[i]);
            String correctBlank = normalizeText(correctBlanks[i]);

            normalizedStudentList.add(studentBlank);
            normalizedCorrectList.add(correctBlank);

            if (studentBlank.equals(correctBlank)) {
                correctCount++;
            }

            log.debug("填空[{}] - 学生:{} → {}, 正确:{} → {}, 匹配:{}",
                    i + 1,
                    studentBlanks[i].trim(), studentBlank,
                    correctBlanks[i].trim(), correctBlank,
                    studentBlank.equals(correctBlank));
        }

        // 计算部分得分
        BigDecimal itemScore = BigDecimal.valueOf((double) correctCount / totalCount)
                .setScale(4, RoundingMode.HALF_UP);
        boolean isFullyCorrect = (correctCount == totalCount);

        log.info("填空题判断完成 - 正确:{}/{}, 得分:{}, 全对:{}",
                correctCount, totalCount, itemScore, isFullyCorrect);

        return new JudgeResult()
                .setFullyCorrect(isFullyCorrect)
                .setItemScore(itemScore)
                .setCorrectCount(correctCount)
                .setTotalCount(totalCount)
                .setNormalizedStudentAnswer(String.join(";", normalizedStudentList))
                .setNormalizedCorrectAnswer(String.join(";", normalizedCorrectList));
    }

    /**
     * 标准化文本：去除所有空格，转小写
     */
    private static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        // 去除所有空白字符（空格、制表符、换行符等），转小写
        return text.replaceAll("\\s+", "").toUpperCase();
    }

    /**
     * 判断结果类（艹，这个结果包含了所有需要的信息）
     */
    @Data
    @Accessors(chain = true)
    public static class JudgeResult {
        /**
         * 是否全对（用于isCorrect字段）
         */
        private Boolean fullyCorrect;

        /**
         * 部分得分（0.00-1.00，用于itemScore字段）
         */
        private BigDecimal itemScore;

        /**
         * 正确的填空数
         */
        private Integer correctCount;

        /**
         * 总填空数
         */
        private Integer totalCount;

        /**
         * 标准化后的学生答案（用于日志和调试）
         */
        private String normalizedStudentAnswer;

        /**
         * 标准化后的正确答案（用于日志和调试）
         */
        private String normalizedCorrectAnswer;

        /**
         * 创建一个全错的结果
         */
        public static JudgeResult allWrong() {
            return new JudgeResult()
                    .setFullyCorrect(false)
                    .setItemScore(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP))
                    .setCorrectCount(0)
                    .setTotalCount(1)
                    .setNormalizedStudentAnswer("")
                    .setNormalizedCorrectAnswer("");
        }
    }
}
