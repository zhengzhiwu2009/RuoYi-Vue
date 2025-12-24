package com.ruoyi.ailearn.course.controller;

import com.ruoyi.ailearn.assessment.vo.AssessmentReportVO;
import com.ruoyi.ailearn.course.model.vo.KpointMasteryVO;
import com.ruoyi.ailearn.course.model.vo.KpointVO;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ailearn.course.domain.Kpoint;
import com.ruoyi.ailearn.course.service.KpointService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: LiuYang
 * @create: 2025-12-04 10:55
 **/

@RestController
@RequestMapping("/api/kpoint")
@Api(tags = "知识点")
public class KpointController {

    @Autowired
    private KpointService kpointService;

    @GetMapping("/{id}")
    public R<KpointVO> getKpointDetail(@PathVariable @ApiParam(value = "知识点ID", required = true) Long id) {
        return R.ok(kpointService.getKpointDetail(id));
    }

    @RequestMapping("/update")
    public R<Boolean> updateKpoint(@RequestBody Kpoint kpoint) {
        return R.ok(kpointService.updateKpoint(kpoint));
    }

    @GetMapping("/masteryList/{chapterId}")
    @ApiOperation(value = "获取知识点掌握情况列表", notes = "查询章节下所有知识点及当前用户的详细掌握情况，包括掌握率、能力值、测评统计等")
    public R<List<KpointMasteryVO>> getKpointMasteryList(
            @ApiParam(value = "章节ID", required = true) @PathVariable(value = "chapterId") Long chapterId) {
        Long studentId = SecurityUtils.getUserId();
        return R.ok(kpointService.getKpointMasteryList(chapterId, studentId));
    }
}
