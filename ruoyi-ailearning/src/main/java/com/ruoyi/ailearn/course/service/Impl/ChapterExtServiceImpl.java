package com.ruoyi.ailearn.course.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.mapper.ChapterMapper;
import com.ruoyi.ailearn.course.model.vo.ChapterVO;
import com.ruoyi.ailearn.course.service.ChapterExtService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-08 10:19
 **/

@Service
public class ChapterExtServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterExtService {


    @Override
    public Chapter getById(Long chapterId) {
        return super.getById(chapterId);
    }

    @Override
    public boolean updateById(Chapter chapter) {
        return super.updateById(chapter);
    }

    @Override
    public List<ChapterVO> getChapterList(Long courseId) {
        return this.baseMapper.getChapterList(courseId);
    }

    @Override
    public List<ChapterVO> getChapterListWithMastery(Long courseId, Long studentId) {
        return this.baseMapper.getChapterListWithMastery(courseId, studentId);
    }
}

