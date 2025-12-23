package com.ruoyi.ailearn.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.model.vo.ChapterVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:48
 **/
public interface ChapterMapper extends BaseMapper<Chapter> {
    /**
     * 获取章节列表
     * @param courseId 课程id
     * @return 章节列表
     */
    List<ChapterVO> getChapterList(@Param("courseId") Long courseId);

    /**
     * 获取章节列表（带知识点统计和用户掌握情况）
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 章节列表
     */
    List<ChapterVO> getChapterListWithMastery(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
