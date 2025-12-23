package com.ruoyi.ailearn.user.service;

import com.ruoyi.ailearn.user.model.vo.RankingResponse;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:23
 **/
public interface UserService {
    /**
     * 获取排行榜
     * @return
     */
    RankingResponse getRankingList(Long userId);
}
