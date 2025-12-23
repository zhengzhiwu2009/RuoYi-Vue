package com.ruoyi.ailearn.course.model.vo;

import lombok.Data;

/**
 * @author: LiuYang
 * @create: 2025-12-03 14:17
 **/

@Data
public class AnswerItem {
    private Boolean isCorrect;
    private String answer;
    private String userAnswer;
    private Boolean hasMathFormular;
}

