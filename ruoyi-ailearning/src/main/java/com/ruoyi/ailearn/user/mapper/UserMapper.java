package com.ruoyi.ailearn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.user.domain.User;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:24
 **/
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询某用户的全局排名（同分处理：Id 小的排前面）
     * @param userId
     * @return
     */
    Integer getUserRankByScore(Integer score, Long userId);
}
