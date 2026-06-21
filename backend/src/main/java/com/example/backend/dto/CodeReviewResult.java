package com.example.backend.dto;

import lombok.Data;

/**
 * AI 代码审查结果
 */
@Data
public class CodeReviewResult {
    /** 是否真正解题而非面向结果硬编码 */
    private boolean isGenuineSolution;
    /** AI 给出的代码质量评分 (0-100) */
    private int score;
    /** 审查反馈意见 */
    private String feedback;
    /** 优点和不足 */
    private String strengths;
    private String weaknesses;
}
