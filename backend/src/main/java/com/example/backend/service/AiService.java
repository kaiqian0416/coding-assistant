package com.example.backend.service;

import com.example.backend.dto.CodeReviewResult;
import com.example.backend.entity.Question;

/**
 * AI 服务：调用 DeepSeek API 进行代码诊断、审查和题目生成
 */
public interface AiService {

    /** 代码错误诊断 */
    String diagnose(String code, String errorInfo, Question question);

    /**
     * AI 审查代码是否真正解题（防止面向结果编程）
     *
     * @param code     用户提交的代码
     * @param question 题目信息
     * @param outputOk 代码执行输出是否匹配预期
     * @return 审查结果
     */
    CodeReviewResult reviewCode(String code, Question question, boolean outputOk);

    /** AI 生成题目 */
    String generateQuestion(String knowledgePoint, String difficulty, String topic);
}
