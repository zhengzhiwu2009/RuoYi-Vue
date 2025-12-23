package com.ruoyi.ailearn.user.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.ailearn.commond.bean.AssignIdBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@Accessors(chain = true)
@TableName(value = "user", autoResultMap = true)
@ApiModel(value = "User对象", description = "系统用户信息")
public class User extends AssignIdBaseEntity {

    @ApiModelProperty(value = "注册账号")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "盐")
    @TableField("salt")
    private String salt;

    @ApiModelProperty(value = "手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "性别：0-未知，1-男，2-女")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    private Date birthday;

    @ApiModelProperty(value = "个人简介")
    @TableField("bio")
    private String bio;

    @ApiModelProperty(value = "积分")
    @TableField("score")
    private Integer score;

    @ApiModelProperty(value = "当前连续签到天数")
    @TableField("successions")
    private Integer successions;

    @ApiModelProperty(value = "历史最大连续签到天数")
    @TableField("maxsuccessions")
    private Integer maxsuccessions;

    @ApiModelProperty(value = "最后登录时间")
    @TableField("logintime")
    private LocalDateTime logintime;

    @ApiModelProperty(value = "最后登录IP")
    @TableField("loginip")
    private String loginip;

    @ApiModelProperty(value = "注册时间")
    @TableField("jointime")
    private LocalDateTime jointime;

    @ApiModelProperty(value = "资料最后更新时间")
    @TableField("updatetime")
    private LocalDateTime updatetime;

    @ApiModelProperty(value = "账户状态(0-无效,1-正常)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "用户类型：如1-教师，2-学生")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "是否开通课程权限：1-是，0-否")
    @TableField("iscourse")
    private Boolean iscourse;

    @ApiModelProperty(value = "是否开通资源权限：1-是，0-否")
    @TableField("resource")
    private Boolean resource;

    @ApiModelProperty(value = "注册来源：如1-微信，2-手机号")
    @TableField("source")
    private Integer source;

    @ApiModelProperty(value = "是否参与排行榜：1-参与，0-不参与")
    @TableField("isrank")
    private Boolean isrank;

    @ApiModelProperty(value = "账号过期时间")
    @TableField("expiredtime")
    private LocalDateTime expiredtime;

    @ApiModelProperty(value = "学校")
    @TableField("school")
    private String school;

    @ApiModelProperty(value = "年级")
    @TableField("grade")
    private String grade;

    @ApiModelProperty(value = "地区")
    @TableField("area")
    private String area;

    @ApiModelProperty(value = "用户角色列表")
    @TableField("roles")
    private String roles;
}

