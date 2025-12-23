package com.ruoyi.ailearn.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.course.domain.Kpoint;
import com.ruoyi.ailearn.course.model.vo.KpointMasteryVO;
import com.ruoyi.ailearn.course.model.vo.KpointVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:52
 **/
public interface KpointMapper extends BaseMapper<Kpoint> {

    /**
     * 获取知识点列表
     * @param chapterId
     * @return
     */
    List<KpointVO> getKpointList(@Param("chapterId") Long chapterId);

    /**
     * 获取知识点详情
     * @param kpointId
     * @return
     */
    KpointVO getKpoint(@Param("kpointId") Long kpointId);

    /**
     * 获取章节下知识点的掌握情况列表（包含详细的掌握数据）
     * @param chapterId 章节ID
     * @param studentId 学生ID
     * @return 知识点掌握情况列表
     */
    List<KpointMasteryVO> getKpointMasteryList(@Param("chapterId") Long chapterId, @Param("studentId") Long studentId);
}
