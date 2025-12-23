package com.ruoyi.ailearn.course.service;

import com.ruoyi.ailearn.course.model.vo.QuestionVO;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-02 09:54
 **/
public interface QuestionService {
    /**
     * 获取题目列表
     * @param kpointId
     * @return
     */
    List<QuestionVO> getQuestionList(Long kpointId);
}
