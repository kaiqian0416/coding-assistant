package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 代码提交请求
 */
@Data
public class SubmissionDTO {

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotBlank(message = "代码不能为空")
    private String code;

    private String language = "python";
}
