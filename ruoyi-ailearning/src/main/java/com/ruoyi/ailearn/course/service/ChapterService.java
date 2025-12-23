package com.ruoyi.ailearn.course.service;

import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.model.vo.ChapterVO;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:47
 **/

public interface ChapterService {
    /**
     * 获取章节详情
     * @param chapterId 章节ID
     * @return 章节详情
     */
    ChapterVO getChapterDetail(Long chapterId);

    /**
     * 获取章节列表
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<ChapterVO> getChapterList(Long courseId);

    /**
     * 获取章节列表（带知识点统计和用户掌握情况）
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 章节列表
     */
    List<ChapterVO> getChapterListWithMastery(Long courseId, Long studentId);

    /**
     * 修改章节
     * @param chapter 章节信息
     * @return 修改结果
     */
    Boolean updateChapter(Chapter chapter);
}

