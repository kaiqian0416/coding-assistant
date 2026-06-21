package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.Question;
import com.example.backend.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 普通用户题目接口（仅公开操作）
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /** 获取已审核通过的题目列表（支持按难度/知识点筛选） */
    @GetMapping
    public ApiResponse<List<Question>> listQuestions(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String knowledgePoint) {
        return ApiResponse.success(questionService.getApprovedQuestions(difficulty, knowledgePoint));
    }

    /** 获取题目详情 */
    @GetMapping("/{id}")
    public ApiResponse<Question> getQuestion(@PathVariable Long id) {
        Question question = questionService.getById(id);
        if (question == null) return ApiResponse.error("题目不存在");
        return ApiResponse.success(question);
    }

    /** 获取个性化推荐题目 */
    @GetMapping("/recommended")
    public ApiResponse<List<Question>> getRecommended(@RequestParam Long userId) {
        return ApiResponse.success(questionService.getRecommendedQuestions(userId));
    }
}
