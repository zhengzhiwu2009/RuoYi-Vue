package com.ruoyi.ailearn.user.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-11 17:37
 **/

@Data
@Accessors(chain = true)
public class RankingResponse {
    @ApiModelProperty("排行榜列表（按积分降序）")
    private List<UserRankItem> rankingList;

    @ApiModelProperty("当前用户排名信息，未上榜则为 null")
    private UserRankItem currentUser;
}

