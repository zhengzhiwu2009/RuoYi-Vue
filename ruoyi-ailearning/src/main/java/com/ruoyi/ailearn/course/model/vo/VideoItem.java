package com.ruoyi.ailearn.course.model.vo;

import lombok.Data;

/**
 * @author: LiuYang
 * @create: 2025-12-09 18:05
 **/

@Data
public class VideoItem {
    private Long id;
    private String url;
    private String bucket;
    private String name;
    private Long playNumbers;
    private Integer duration; // 单位：秒
}

