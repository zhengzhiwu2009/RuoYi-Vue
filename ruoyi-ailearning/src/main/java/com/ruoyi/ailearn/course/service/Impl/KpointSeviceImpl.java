package com.ruoyi.ailearn.course.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.course.domain.Kpoint;
import com.ruoyi.ailearn.course.mapper.KpointMapper;
import com.ruoyi.ailearn.course.model.vo.KpointMasteryVO;
import com.ruoyi.ailearn.course.model.vo.KpointVO;
import com.ruoyi.ailearn.course.model.vo.VideoItem;
import com.ruoyi.ailearn.course.service.ChapterExtService;
import com.ruoyi.ailearn.course.service.KpointService;
import com.ruoyi.ailearn.course.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:50
 **/

@Slf4j
@Service
public class KpointSeviceImpl extends ServiceImpl<KpointMapper, Kpoint> implements KpointService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChapterExtService chapterService;

    private final double MAX_MASTERY_RATE = 0.8;

    @Override
    public List<KpointVO> getKpointList(Long chapterId) {
        List<KpointVO> kpointVOList = this.baseMapper.getKpointList(chapterId);
        if(kpointVOList.isEmpty()){
            return Collections.emptyList();
        }
        return kpointVOList.stream().peek(this::parseJSON).collect(Collectors.toList());
    }

    @Override
    public KpointVO getKpointDetail(Long kpointId) {
        KpointVO kpointVO = this.baseMapper.getKpoint(kpointId);
        kpointVO.setQuestionList(questionService.getQuestionList(kpointId));
        return parseJSON(kpointVO);
    }

    private KpointVO parseJSON(KpointVO kpointVO) {
        if (StringUtils.hasText(kpointVO.getVideoJSON())) {
            try {
                VideoItem videoItem = JSON.parseObject(
                        kpointVO.getVideoJSON(),
                        new TypeReference<VideoItem>() {}
                );
                kpointVO.setVideo(videoItem);
            } catch (Exception e) {
                log.warn("解析 videoJson 失败, id={}, json={}", kpointVO.getKpointId(), kpointVO.getVideoJSON(), e);
                kpointVO.setVideo(null);
            }
        } else {
            kpointVO.setVideo(null);
        }
        return kpointVO;
    }

    @Override
    public Boolean updateKpoint(Kpoint kpoint) {
        return this.updateById(kpoint);
    }

    @Override
    public List<KpointMasteryVO> getKpointMasteryList(Long chapterId, Long studentId) {
        // 艹！直接调用Mapper查询，简单直接，不搞花里胡哨的！
        return this.baseMapper.getKpointMasteryList(chapterId, studentId);
    }
}

