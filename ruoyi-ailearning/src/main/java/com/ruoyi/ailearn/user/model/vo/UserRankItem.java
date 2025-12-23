package com.ruoyi.ailearn.user.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: LiuYang
 * @create: 2025-12-11 18:01
 **/

@Data
public class UserRankItem {
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("积分")
    private Integer score;

    @ApiModelProperty("排名（从1开始）")
    private Integer rank;
}

