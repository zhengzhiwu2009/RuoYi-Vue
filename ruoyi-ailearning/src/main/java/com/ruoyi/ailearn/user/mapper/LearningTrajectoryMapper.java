package com.ruoyi.ailearn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.user.domain.LearningTrajectory;
import com.ruoyi.ailearn.user.model.vo.LearningTrajectoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:20
 **/

public interface LearningTrajectoryMapper extends BaseMapper<LearningTrajectory> {
    /**
     * 获取用户学习轨迹
     *
     * @param userId
     * @param category
     * @param date
     * @return
     */
    LearningTrajectoryVO getLearningTrajectory(@Param("userId") Long userId, @Param("category") int category, @Param("date") Date date);

    /**
     * 获取用户学习轨迹日期列表
     *
     * @param userId
     * @param category
     * @return
     */
    List<Date> getLearningTrajectoryDateList(@Param("userId") Long userId, @Param("category") int category);
}
