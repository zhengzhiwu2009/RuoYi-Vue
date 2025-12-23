package com.ruoyi.ailearn.user.controller;

import com.ruoyi.ailearn.user.service.LearningTrajectoryService;
import com.ruoyi.ailearn.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: LiuYang
 * @create: 2025-12-10 17:23
 **/

@RestController
@RequestMapping("/user")
@Api(tags = "用户")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LearningTrajectoryService learningTrajectoryService;

//    @RequestMapping("/learning-trajectory")
//    public Response getLearningTrajectory(@RequestParam(value = "userId") Long userId,
//                                          @RequestParam(value = "category") int category,
//                                          @RequestParam(value = "date") Date date) {
//        // TODO 理论上来说应该在此处获取 当前用户的userId ,而不是在请求中获取
//        return ResponseFactory.FACTORY.buildSuccess(learningTrajectoryService.getLearningTrajectory(userId, category, date));
//    }
//
//    @RequestMapping("/learning-trajectory/date")
//    public Response getLearningTrajectoryDateList(@RequestParam(value = "userId") Long userId,
//                                                  @RequestParam(value = "category") int category) {
//        // TODO 理论上来说应该在此处获取 当前用户的userId ,而不是在请求中获取
//        return ResponseFactory.FACTORY.buildSuccess(learningTrajectoryService.getLearningTrajectoryDateList(userId, category));
//    }
//
//    @RequestMapping("/ranking-list")
//    public Response<RankingResponse> getRankingList(@RequestParam(value = "userId") Long userId) {
//        // TODO 理论上来说应该在此处获取 当前用户的userId ,而不是在请求中获取
//        return ResponseFactory.FACTORY.buildSuccess(userService.getRankingList(userId));
//    }
}

