package com.ruoyi.ailearn.course.service;

import com.ruoyi.ailearn.course.domain.Kpoint;
import com.ruoyi.ailearn.course.model.vo.KpointMasteryVO;
import com.ruoyi.ailearn.course.model.vo.KpointVO;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:50
 **/
public interface KpointService {
    /**
     * 获取知识点列表
     * @param chapterId 章节id
     * @return 知识点列表
     */
    List<KpointVO> getKpointList(Long chapterId);

    /**
     * 获取知识点详情
     * @param kpointId 知识点id
     * @return 知识点详情
     */
    KpointVO getKpointDetail(Long kpointId);

    /**
     * 修改知识点
      * @param kpoint 知识点
     * @return 修改结果
     */
    Boolean updateKpoint(Kpoint kpoint);

    /**
     * 获取章节下知识点的掌握情况列表（包含详细的掌握数据）
     * @param chapterId 章节ID
     * @param studentId 学生ID
     * @return 知识点掌握情况列表
     */
    List<KpointMasteryVO> getKpointMasteryList(Long chapterId, Long studentId);
}
