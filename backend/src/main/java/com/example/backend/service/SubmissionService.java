package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.SubmissionDTO;
import com.example.backend.entity.Submission;

import java.util.List;
import java.util.Map;

public interface SubmissionService extends IService<Submission> {

    /**
     * 提交代码并判题
     * @return 提交记录（包含判题结果和 AI 诊断）
     */
    Submission submitCode(Long userId, SubmissionDTO dto);

    /**
     * 获取用户的提交历史
     */
    List<Submission> getUserSubmissions(Long userId);

    /**
     * 获取题目的所有提交
     */
    List<Submission> getQuestionSubmissions(Long questionId);

    /**
     * 获取用户在某道题上的提交历史
     */
    List<Submission> getUserQuestionSubmissions(Long userId, Long questionId);

    /**
     * 获取用户提交统计数据
     */
    Map<String, Object> getUserSubmissionStats(Long userId);
}
