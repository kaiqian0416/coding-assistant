package com.example.backend.controller;

import com.example.backend.config.SessionInterceptor;
import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.Achievement;
import com.example.backend.entity.User;
import com.example.backend.mapper.AchievementMapper;
import com.example.backend.mapper.SubmissionMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementMapper achievementMapper;
    private final SubmissionMapper submissionMapper;

    public AchievementController(AchievementMapper achievementMapper, SubmissionMapper submissionMapper) {
        this.achievementMapper = achievementMapper;
        this.submissionMapper = submissionMapper;
    }

    /** 获取用户成就列表（计算哪些已获得） */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAchievements(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ApiResponse.unauthorized("未登录");
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);

        // 从数据库读取成就定义
        List<Achievement> defs = achievementMapper.selectList(null);
        if (defs.isEmpty()) return ApiResponse.success(List.of());

        // 计算用户统计数据
        var subs = submissionMapper.findByUserId(user.getId());
        long total = subs.size();
        long accepted = subs.stream().filter(s -> "accepted".equals(s.getResult())).count();
        long wrong = total - accepted;

        Map<String, Long> stats = Map.of(
            "total_submit", total,
            "total_accepted", accepted,
            "total_wrong", wrong
        );

        List<Map<String, Object>> result = defs.stream()
            .sorted(Comparator.comparingInt(Achievement::getSortOrder))
            .map(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", a.getId());
                m.put("name", a.getName());
                m.put("description", a.getDescription());
                m.put("icon", a.getIcon());
                m.put("conditionType", a.getConditionType());
                m.put("conditionValue", a.getConditionValue());
                long actual = stats.getOrDefault(a.getConditionType(), 0L);
                m.put("earned", actual >= a.getConditionValue());
                m.put("progress", Math.min(100, (int)(actual * 100 / Math.max(1, a.getConditionValue()))));
                return m;
            })
            .collect(Collectors.toList());

        return ApiResponse.success(result);
    }
}
