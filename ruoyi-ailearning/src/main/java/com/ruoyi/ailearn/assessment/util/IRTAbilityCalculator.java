package com.ruoyi.ailearn.assessment.util;

import com.ruoyi.ailearn.assessment.domain.AnswerDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * IRT能力值计算器
 * 基于项目反应理论（Item Response Theory）的能力值估计
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 *
 * 艹，这是核心算法！用牛顿-拉夫森迭代法计算能力值！
 */
@Slf4j
@Component
public class IRTAbilityCalculator {

    /**
     * 计算能力值和能力区间
     *
     * @param answerHistory 答题历史列表
     * @return 能力计算结果
     */
    public AbilityResult calculateAbility(List<AnswerDetail> answerHistory) {
        if (answerHistory == null || answerHistory.isEmpty()) {
            throw new IllegalArgumentException("答题历史不能为空");
        }

        // 1. 极大似然估计（MLE）计算能力值
        double theta = estimateAbilityMLE(answerHistory);

        // 2. 计算信息函数
        double information = calculateInformation(theta, answerHistory);

        // 3. 计算标准误差
        double standardError = information > 0 ? 1.0 / Math.sqrt(information) : 0.5;

        // 4. 计算95%置信区间
        double zScore = 1.96;  // 95%置信水平
        double lowerBound = Math.max(0.0, theta - zScore * standardError);
        double upperBound = Math.min(1.0, theta + zScore * standardError);

        // 5. 计算区间宽度
        double intervalWidth = upperBound - lowerBound;

        // 6. 计算信心水平（基于信息量，归一化到0-1）
        double confidence = Math.min(1.0, information / 10.0);

        // 7. 构建结果对象
        AbilityResult result = new AbilityResult();
        result.setAbility(toBigDecimal(theta));
        result.setLowerBound(toBigDecimal(lowerBound));
        result.setUpperBound(toBigDecimal(upperBound));
        result.setIntervalWidth(toBigDecimal(intervalWidth));
        result.setConfidence(toBigDecimal(confidence));
        result.setStandardError(toBigDecimal(standardError));

        log.debug("IRT能力计算完成 - 能力值:{}, 区间:[{}, {}], 信心:{}",
                result.getAbility(), result.getLowerBound(),
                result.getUpperBound(), result.getConfidence());

        return result;
    }

    /**
     * 极大似然估计（MLE）计算能力值
     * 使用牛顿-拉夫森迭代法
     *
     * @param history 答题历史
     * @return 能力估计值
     */
    private double estimateAbilityMLE(List<AnswerDetail> history) {
        double theta = 0.5;  // 初始能力值（中等水平）

        // 牛顿-拉夫森迭代，最多20次
        for (int iteration = 0; iteration < 20; iteration++) {
            double gradient = 0.0;      // 一阶导数
            double hessian = 0.0;       // 二阶导数

            for (AnswerDetail item : history) {
                // 获取题目参数
                double a = getDiscrimination(item);  // 区分度
                double b = getDifficulty(item);      // 难度
                double c = getGuessing(item);        // 猜测度

                // 计算答对概率 P(θ)
                double p = calculateProbability(theta, a, b, c);

                // 根据实际答题情况计算梯度和海森矩阵
                if (Boolean.TRUE.equals(item.getIsCorrect())) {
                    // 答对了
                    gradient += a * (1 - p) / p;
                    hessian -= a * a * (1 - p) / (p * p);
                } else {
                    // 答错了
                    gradient -= a * p / (1 - p);
                    hessian -= a * a * p / ((1 - p) * (1 - p));
                }
            }

            // 更新能力值
            if (Math.abs(hessian) > 0.001) {
                double thetaNew = theta - gradient / hessian;

                // 限制在[0, 1]范围内
                thetaNew = Math.max(0.0, Math.min(1.0, thetaNew));

                // 收敛判断
                if (Math.abs(thetaNew - theta) < 0.001) {
                    theta = thetaNew;
                    log.debug("MLE迭代收敛 - 迭代次数:{}, 能力值:{}", iteration + 1, theta);
                    break;
                }

                theta = thetaNew;
            } else {
                log.warn("海森矩阵接近0，终止迭代");
                break;
            }
        }

        return theta;
    }

    /**
     * 计算信息函数 I(θ)
     * 信息量越大，估计越准确
     *
     * @param theta   能力值
     * @param history 答题历史
     * @return 信息量
     */
    private double calculateInformation(double theta, List<AnswerDetail> history) {
        double information = 0.0;

        for (AnswerDetail item : history) {
            double a = getDiscrimination(item);
            double b = getDifficulty(item);
            double c = getGuessing(item);

            // 计算答对概率
            double p = calculateProbability(theta, a, b, c);

            // 累加信息量：I(θ) = a² * P(θ) * (1 - P(θ)) / (1 - c)²
            // 简化版（c=0时）：I(θ) = a² * P(θ) * (1 - P(θ))
            if (c < 0.001) {
                information += a * a * p * (1 - p);
            } else {
                information += a * a * p * (1 - p) / ((1 - c) * (1 - c));
            }
        }

        return information;
    }

    /**
     * IRT 2参数/3参数模型的答对概率
     * P(θ) = c + (1-c) / (1 + e^(-a(θ-b)))
     *
     * @param theta 能力值
     * @param a     区分度
     * @param b     难度
     * @param c     猜测度
     * @return 答对概率
     */
    private double calculateProbability(double theta, double a, double b, double c) {
        try {
            double exponent = -a * (theta - b);
            double probability = c + (1 - c) / (1 + Math.exp(exponent));

            // 避免概率为0或1（导致对数计算错误）
            return Math.max(0.001, Math.min(0.999, probability));
        } catch (Exception e) {
            log.error("计算概率失败 - theta:{}, a:{}, b:{}, c:{}", theta, a, b, c, e);
            return 0.5;  // 返回默认值
        }
    }

    /**
     * 获取题目区分度（默认1.5）
     */
    private double getDiscrimination(AnswerDetail item) {
        if (item.getDiscrimination() != null) {
            return item.getDiscrimination().doubleValue();
        }
        return 1.5;  // 默认区分度
    }

    /**
     * 获取题目难度
     */
    private double getDifficulty(AnswerDetail item) {
        if (item.getDifficulty() != null) {
            return item.getDifficulty().doubleValue();
        }
        return 0.5;  // 默认中等难度
    }

    /**
     * 获取题目猜测度（默认0，表示不考虑猜测）
     */
    private double getGuessing(AnswerDetail item) {
        // answer_detail表中没有guessing字段，但在测试中可以设置
        // 生产环境中需要从question表查询
        if (item.getGuessing() != null) {
            return item.getGuessing().doubleValue();
        }
        return 0.0;  // 默认不考虑猜测
    }

    /**
     * double转BigDecimal（保留4位小数）
     */
    private BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }
}
