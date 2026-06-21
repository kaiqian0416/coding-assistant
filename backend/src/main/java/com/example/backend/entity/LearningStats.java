package com.example.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习统计实体
 */
@Data
@TableName("learning_stats")
public class LearningStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String knowledgePoint;

    private Integer totalCount;

    private Integer correctCount;

    private Integer wrongCount;

    private Double accuracy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
