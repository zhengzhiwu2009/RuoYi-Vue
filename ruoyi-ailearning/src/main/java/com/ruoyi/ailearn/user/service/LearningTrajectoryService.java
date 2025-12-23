package com.ruoyi.ailearn.user.service;

import com.ruoyi.ailearn.user.model.vo.LearningTrajectoryVO;

import java.util.Date;
import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:20
 **/
public interface LearningTrajectoryService {
    /**
     * 获取用户学习轨迹
     *
     * @param userId
     * @param category
     * @param date
     * @return
     */
    LearningTrajectoryVO getLearningTrajectory(Long userId, int category, Date date);

    /**
     * 获取用户学习轨迹日期列表
     *
     * @param userId
     * @param category
     * @return
     */
    List<Date> getLearningTrajectoryDateList(Long userId, int category);
}
