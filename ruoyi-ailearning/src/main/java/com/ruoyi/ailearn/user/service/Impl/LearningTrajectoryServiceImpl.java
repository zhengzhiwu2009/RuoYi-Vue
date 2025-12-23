package com.ruoyi.ailearn.user.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.user.domain.LearningTrajectory;
import com.ruoyi.ailearn.user.mapper.LearningTrajectoryMapper;
import com.ruoyi.ailearn.user.model.vo.LearningTrajectoryVO;
import com.ruoyi.ailearn.user.service.LearningTrajectoryService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:20
 **/

@Service
public class LearningTrajectoryServiceImpl extends ServiceImpl<LearningTrajectoryMapper, LearningTrajectory>
        implements LearningTrajectoryService {

    @Override
    public LearningTrajectoryVO getLearningTrajectory(Long userId, int category, Date date) {
        return this.baseMapper.getLearningTrajectory(userId, category, date);
    }

    @Override
    public List<Date> getLearningTrajectoryDateList(Long userId, int category) {
        return this.baseMapper.getLearningTrajectoryDateList(userId, category);
    }
}

