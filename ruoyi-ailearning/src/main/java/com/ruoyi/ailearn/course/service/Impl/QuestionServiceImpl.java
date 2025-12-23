package com.ruoyi.ailearn.course.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.ailearn.course.model.vo.AnswerItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.course.domain.Question;
import com.ruoyi.ailearn.course.mapper.QuestionMapper;
import com.ruoyi.ailearn.course.model.vo.QuestionVO;
import com.ruoyi.ailearn.course.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: LiuYang
 * @create: 2025-12-02 09:55
 **/

@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public List<QuestionVO> getQuestionList(Long kpointId) {
        List<QuestionVO> questionList = this.baseMapper.getQuestionList(kpointId);
        return questionList.stream().peek(questionVO -> {
            // 解析 options JSON 字符串
            if (StringUtils.hasText(questionVO.getOptionsJson())) {
                try {
                    List<String> optionsList = JSON.parseObject(
                            questionVO.getOptionsJson(),
                            new TypeReference<List<String>>() {}
                    );
                    questionVO.setOptions(optionsList);
                } catch (Exception e) {
                    log.warn("解析 options 失败, id={}, json={}", questionVO.getId(), questionVO.getOptionsJson(), e);
                    questionVO.setOptions(Collections.emptyList());
                }
            } else {
                questionVO.setOptions(Collections.emptyList());
            }
            // 解析 answer_items JSON 字符串
            if (StringUtils.hasText(questionVO.getAnswerJson())) {
                try {
                    List<AnswerItem> answerList = JSON.parseObject(
                            questionVO.getAnswerJson(),
                            new TypeReference<List<AnswerItem>>() {}
                    );
                    questionVO.setAnswer(answerList);
                } catch (Exception e) {
                    log.warn("解析 answerJson 失败, id={}, json={}", questionVO.getId(), questionVO.getAnswerJson(), e);
                    questionVO.setAnswer(Collections.emptyList());
                }
            } else {
                questionVO.setAnswer(Collections.emptyList());
            }
        }).collect(Collectors.toList());
    }
}
