package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提交记录实体
 */
@Data
@TableName("submission")
public class Submission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long questionId;

    private String code;

    private String language;

    private String result;

    private Integer score;

    private String errorInfo;

    private String aiDiagnosis;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
