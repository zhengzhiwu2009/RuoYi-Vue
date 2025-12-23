package com.ruoyi.ailearn.user.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ailearn.user.domain.User;
import com.ruoyi.ailearn.user.mapper.UserMapper;
import com.ruoyi.ailearn.user.model.vo.RankingResponse;
import com.ruoyi.ailearn.user.model.vo.UserRankItem;
import com.ruoyi.ailearn.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:23
 **/

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public RankingResponse getRankingList(Long userId) {
        if (userId == null) {
            return null;
        }
        List<User> users = this.baseMapper.selectList(lambdaQuery().orderByDesc(User::getScore).last("limit 100"));

        List<UserRankItem> rankingList = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            rankingList.add(toUserRankItem(u, i + 1));
        }

        User currentUser = this.baseMapper.selectById(userId);
        UserRankItem currentUserItem = null;
        if (currentUser != null && currentUser.getStatus() == 1) {
            // 获取当前用户的真实排名（比他积分高的用户数量 + 1）
            Integer userRank = this.baseMapper.getUserRankByScore(currentUser.getScore(), userId);
            currentUserItem = toUserRankItem(currentUser, userRank);
        }

        return new RankingResponse().setRankingList(rankingList).setCurrentUser(currentUserItem);
    }

    /**
     * 转换为排行榜项
     *
     * @param user
     * @param rank
     * @return
     */
    private UserRankItem toUserRankItem(User user, Integer rank) {
        if (user == null) {
            return null;
        }
        UserRankItem item = new UserRankItem();
        item.setUserId(user.getId());
        item.setNickname(user.getNickname());
        item.setAvatar(user.getAvatar());
        item.setScore(user.getScore());
        item.setRank(rank);
        return item;
    }
}

