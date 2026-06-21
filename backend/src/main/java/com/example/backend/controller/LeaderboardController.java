package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.mapper.SubmissionMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final SubmissionMapper submissionMapper;

    public LeaderboardController(SubmissionMapper submissionMapper) {
        this.submissionMapper = submissionMapper;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> getLeaderboard() {
        return ApiResponse.success(submissionMapper.leaderboard());
    }
}
