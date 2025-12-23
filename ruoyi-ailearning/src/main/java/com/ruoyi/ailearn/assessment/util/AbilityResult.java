package com.ruoyi.ailearn.assessment.util;

import lombok.Data;

import java.math.BigDecimal;

/**
 * IRT能力计算结果
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Data
public class AbilityResult {

    /**
     * 能力估计值（0.00-1.00）
     */
    private BigDecimal ability;

    /**
     * 能力区间下限（0.00-1.00）
     */
    private BigDecimal lowerBound;

    /**
     * 能力区间上限（0.00-1.00）
     */
    private BigDecimal upperBound;

    /**
     * 区间宽度
     */
    private BigDecimal intervalWidth;

    /**
     * 信心水平（0.00-1.00）
     * 基于信息量计算，越高表示估计越可靠
     */
    private BigDecimal confidence;

    /**
     * 标准误差
     */
    private BigDecimal standardError;
}
