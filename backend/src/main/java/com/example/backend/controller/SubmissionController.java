package com.example.backend.controller;

import com.example.backend.config.SessionInterceptor;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.SubmissionDTO;
import com.example.backend.entity.Submission;
import com.example.backend.entity.User;
import com.example.backend.mapper.SubmissionMapper;
import com.example.backend.service.SubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;

    public SubmissionController(SubmissionService submissionService, SubmissionMapper submissionMapper) {
        this.submissionService = submissionService;
        this.submissionMapper = submissionMapper;
    }

    @PostMapping
    public ApiResponse<Submission> submitCode(@Valid @RequestBody SubmissionDTO dto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        if ("admin".equals(user.getRole())) return ApiResponse.error("管理员无需提交代码");
        try { return ApiResponse.success("提交成功", submissionService.submitCode(user.getId(), dto));
        } catch (RuntimeException e) { return ApiResponse.error(e.getMessage()); }
    }

    @GetMapping("/my")
    public ApiResponse<List<Submission>> getMySubmissions(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        return ApiResponse.success(submissionService.getUserSubmissions(user.getId()));
    }

    /** 错题本 */
    @GetMapping("/wrong")
    public ApiResponse<List<Map<String, Object>>> getWrongSubmissions(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        return ApiResponse.success(submissionMapper.findWrongByUserId(user.getId()));
    }

    @GetMapping("/question/{questionId}")
    public ApiResponse<List<Submission>> getQuestionSubmissions(@PathVariable Long questionId) {
        return ApiResponse.success(submissionService.getQuestionSubmissions(questionId));
    }

    @GetMapping("/my/question/{questionId}")
    public ApiResponse<List<Submission>> getMyQuestionSubmissions(
            @PathVariable Long questionId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        return ApiResponse.success(submissionService.getUserQuestionSubmissions(user.getId(), questionId));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getSubmissionStats(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        return ApiResponse.success(submissionService.getUserSubmissionStats(user.getId()));
    }
}
