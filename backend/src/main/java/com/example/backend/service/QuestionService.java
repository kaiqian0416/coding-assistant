package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.AiGenerateDTO;
import com.example.backend.entity.Question;

import java.util.List;

public interface QuestionService extends IService<Question> {

    /**
     * 获取已审核通过的题目列表
     */
    List<Question> getApprovedQuestions(String difficulty, String knowledgePoint);

    /**
     * 获取待审核题目（管理员用）
     */
    List<Question> getPendingReviewQuestions();

    /**
     * 审核通过题目
     */
    void approveQuestion(Long questionId);

    /**
     * 驳回题目
     */
    void rejectQuestion(Long questionId);

    /**
     * AI 辅助生成题目
     * @return 生成的题目（尚未审核）
     */
    Question generateByAI(AiGenerateDTO dto, Long adminId);

    /**
     * 获取个性化推荐题目
     */
    List<Question> getRecommendedQuestions(Long userId);
}
