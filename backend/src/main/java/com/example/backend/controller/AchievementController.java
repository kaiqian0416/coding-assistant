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

import java.time.LocalDate;
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

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getAchievements(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ApiResponse.unauthorized("未登录");
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);

        List<Achievement> defs = achievementMapper.selectList(null);
        if (defs.isEmpty()) return ApiResponse.success(List.of());

        var subs = submissionMapper.findByUserId(user.getId());
        long total = subs.size();
        long accepted = subs.stream().filter(s -> "accepted".equals(s.getResult())).count();
        long wrong = total - accepted;
        List<String> dates = submissionMapper.getCheckinDates(user.getId());
        long consecutive = calcConsecutive(dates);

        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total_submit", total);
        stats.put("total_accepted", accepted);
        stats.put("total_wrong", wrong);
        stats.put("consecutive_checkin", consecutive);

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

    private int calcConsecutive(List<String> dates) {
        if (dates == null || dates.isEmpty()) return 0;
        Set<String> set = new HashSet<>(dates);
        LocalDate today = LocalDate.now();
        if (!set.contains(today.toString()) && !set.contains(today.minusDays(1).toString())) return 0;
        LocalDate cursor = set.contains(today.toString()) ? today : today.minusDays(1);
        int count = 0;
        while (set.contains(cursor.toString())) { count++; cursor = cursor.minusDays(1); }
        return count;
    }
}
