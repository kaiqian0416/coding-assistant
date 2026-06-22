package com.example.backend.controller;

import com.example.backend.config.SessionInterceptor;
import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.User;
import com.example.backend.mapper.SubmissionMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    private final SubmissionMapper submissionMapper;

    public CheckinController(SubmissionMapper submissionMapper) {
        this.submissionMapper = submissionMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getCheckin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ApiResponse.unauthorized("未登录");
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);

        int todayCount = submissionMapper.todaySubmissionCount(user.getId());
        List<String> dates = submissionMapper.getCheckinDates(user.getId());
        int consecutive = calcConsecutive(dates);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("checkedIn", todayCount > 0);
        data.put("todayCount", todayCount);
        data.put("consecutiveDays", consecutive);
        data.put("totalCheckinDays", dates.size());
        data.put("dates", dates);
        data.put("date", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return ApiResponse.success(data);
    }

    private int calcConsecutive(List<String> dates) {
        if (dates == null || dates.isEmpty()) return 0;
        Set<String> set = new HashSet<>(dates);
        LocalDate today = LocalDate.now();
        // 今天或昨天必须有打卡记录
        if (!set.contains(today.toString()) && !set.contains(today.minusDays(1).toString())) {
            return 0;
        }
        LocalDate cursor = set.contains(today.toString()) ? today : today.minusDays(1);
        int count = 0;
        while (set.contains(cursor.toString())) {
            count++;
            cursor = cursor.minusDays(1);
        }
        return count;
    }
}
