package com.ruoyi.ailearn.course.controller;

import com.ruoyi.ailearn.course.model.vo.ChapterVO;
import com.ruoyi.ailearn.course.domain.Chapter;
import com.ruoyi.ailearn.course.service.ChapterService;
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
 * @create: 2025-12-04 10:53
 **/

@RestController
@RequestMapping("/api/chapter")
@Api(tags = "章节")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @RequestMapping("/detail")
    public R<ChapterVO> getChapterDetail(@RequestParam(value = "chapterId")Long chapterId) {
        return R.ok(chapterService.getChapterDetail(chapterId));
    }

    @RequestMapping("/update")
    public R<Boolean> updateChapter(@RequestBody Chapter chapter) {
        return R.ok(chapterService.updateChapter(chapter));
    }

    @GetMapping("/listWithMastery/{courseId}")
    @ApiOperation(value = "获取章节列表（带知识点统计和用户掌握情况）", notes = "根据课程ID和学生ID查询章节列表，包含知识点总数和已掌握知识点数")
    public R<List<ChapterVO>> getChapterListWithMastery(
            @ApiParam(value = "课程ID", required = true) @PathVariable(value = "courseId") Long courseId) {
        return R.ok(chapterService.getChapterListWithMastery(courseId, SecurityUtils.getLoginUser().getUserId()));
    }
}

