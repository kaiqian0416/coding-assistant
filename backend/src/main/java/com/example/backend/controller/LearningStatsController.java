package com.example.backend.controller;

import com.example.backend.config.SessionInterceptor;
import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.User;
import com.example.backend.service.LearningStatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习统计接口
 */
@RestController
@RequestMapping("/api/stats")
public class LearningStatsController {

    private final LearningStatsService learningStatsService;

    public LearningStatsController(LearningStatsService learningStatsService) {
        this.learningStatsService = learningStatsService;
    }

    /**
     * 获取用户学习概况
     * GET /api/stats/overview
     */
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        return ApiResponse.success(learningStatsService.getUserOverview(user.getId()));
    }
}
