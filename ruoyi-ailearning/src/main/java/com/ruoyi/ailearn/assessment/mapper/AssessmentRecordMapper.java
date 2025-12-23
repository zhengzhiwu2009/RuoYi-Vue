package com.ruoyi.ailearn.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ailearn.assessment.domain.AssessmentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评记录Mapper
 *
 * @author 老王（暴躁技术流）
 * @date 2025-12-17
 */
@Mapper
public interface AssessmentRecordMapper extends BaseMapper<AssessmentRecord> {
    // MyBatisPlus自动提供基础CRUD方法
    // 不需要额外定义，有特殊需求再加
}
