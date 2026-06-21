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
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    private final SubmissionMapper submissionMapper;

    public CheckinController(SubmissionMapper submissionMapper) {
        this.submissionMapper = submissionMapper;
    }

    /** 获取打卡状态 */
    @GetMapping
    public ApiResponse<Map<String, Object>> getCheckin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return ApiResponse.unauthorized("未登录");
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);

        int todayCount = submissionMapper.todaySubmissionCount(user.getId());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("checkedIn", todayCount > 0);
        data.put("todayCount", todayCount);
        data.put("date", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        return ApiResponse.success(data);
    }
}
