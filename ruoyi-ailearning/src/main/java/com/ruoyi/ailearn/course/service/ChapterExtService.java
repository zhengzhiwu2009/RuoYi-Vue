package com.ruoyi.ailearn.course.service;

import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.model.vo.ChapterVO;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-08 10:19
 **/
public interface ChapterExtService {
    /**
     * 通过章节id获取章节信息
     * @param chapterId
     * @return
     */
    Chapter getById(Long chapterId);

    /**
     * 修改章节信息
     * @param chapter
     * @return
     */
    boolean updateById(Chapter chapter);

    /**
     * 通过课程id获取章节列表
     * @param courseId
     * @return
     */
    List<ChapterVO> getChapterList(Long courseId);

    /**
     * 通过课程ID获取章节列表（带知识点统计和用户掌握情况）
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 章节列表
     */
    List<ChapterVO> getChapterListWithMastery(Long courseId, Long studentId);
}
