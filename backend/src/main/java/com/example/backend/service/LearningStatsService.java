package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.LearningStats;

import java.util.List;
import java.util.Map;

public interface LearningStatsService extends IService<LearningStats> {

    /**
     * 获取用户的所有学习统计
     */
    List<LearningStats> getUserStats(Long userId);

    /**
     * 获取用户学习概况（总题数、正确率、薄弱知识点等）
     */
    Map<String, Object> getUserOverview(Long userId);

    /**
     * 更新用户某知识点的统计数据
     */
    void updateStats(Long userId, String knowledgePoint, boolean isCorrect);
}
