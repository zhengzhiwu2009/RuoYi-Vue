package com.ruoyi.ailearn.course.service.Impl;

import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.model.vo.ChapterVO;
import com.ruoyi.ailearn.course.model.vo.KpointVO;
import com.ruoyi.ailearn.course.service.ChapterExtService;
import com.ruoyi.ailearn.course.service.ChapterService;
import com.ruoyi.ailearn.course.service.KpointService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:47
 **/

@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterExtService baseService;

    @Autowired
    private KpointService kpointService;

    @Override
    public ChapterVO getChapterDetail(Long chapterId) {
        Chapter chapter = baseService.getById(chapterId);
        if (chapter == null) {
            return null;
        }
        ChapterVO chapterVO = new ChapterVO();
        BeanUtils.copyProperties(chapter, chapterVO);
        List<KpointVO> kpointList = kpointService.getKpointList(chapterId);
        chapterVO.setKpointList(kpointList);
        return chapterVO;
    }

    @Override
    public List<ChapterVO> getChapterList(Long courseId) {
        return baseService.getChapterList(courseId);
    }

    @Override
    public List<ChapterVO> getChapterListWithMastery(Long courseId, Long studentId) {
        return baseService.getChapterListWithMastery(courseId, studentId);
    }

    @Override
    public Boolean updateChapter(Chapter chapter) {
        return baseService.updateById(chapter);
    }
}

