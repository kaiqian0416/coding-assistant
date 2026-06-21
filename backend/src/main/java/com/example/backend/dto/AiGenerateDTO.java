package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 生成题目请求
 */
@Data
public class AiGenerateDTO {

    @NotBlank(message = "知识点不能为空")
    private String knowledgePoint;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    @NotBlank(message = "题目主题不能为空")
    private String topic;
}
