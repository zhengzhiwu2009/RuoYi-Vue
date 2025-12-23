package com.ruoyi.ailearn.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.course.domain.Question;
import com.ruoyi.ailearn.course.model.vo.QuestionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-02 10:27
 **/

public interface QuestionMapper extends BaseMapper<Question>  {

    List<QuestionVO> getQuestionList(@Param("kpointId") Long kpointId);
}

