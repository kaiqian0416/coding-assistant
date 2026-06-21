package com.example.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.LearningStats;
import com.example.backend.mapper.LearningStatsMapper;
import com.example.backend.service.LearningStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LearningStatsServiceImpl extends ServiceImpl<LearningStatsMapper, LearningStats>
        implements LearningStatsService {

    private final LearningStatsMapper learningStatsMapper;

    public LearningStatsServiceImpl(LearningStatsMapper learningStatsMapper) {
        this.learningStatsMapper = learningStatsMapper;
    }

    @Override
    public List<LearningStats> getUserStats(Long userId) {
        return learningStatsMapper.findByUserId(userId);
    }

    @Override
    public Map<String, Object> getUserOverview(Long userId) {
        List<LearningStats> statsList = learningStatsMapper.findByUserId(userId);

        int totalCount = statsList.stream().mapToInt(LearningStats::getTotalCount).sum();
        int correctCount = statsList.stream().mapToInt(LearningStats::getCorrectCount).sum();
        int wrongCount = statsList.stream().mapToInt(LearningStats::getWrongCount).sum();

        // 找出最薄弱的知识点（正确率最低且做过题目的）
        List<Map<String, Object>> weakPoints = statsList.stream()
                .filter(s -> s.getTotalCount() > 0)
                .sorted(Comparator.comparing(LearningStats::getAccuracy))
                .limit(3)
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("knowledgePoint", s.getKnowledgePoint());
                    m.put("accuracy", s.getAccuracy());
                    m.put("totalCount", s.getTotalCount());
                    m.put("correctCount", s.getCorrectCount());
                    return m;
                })
                .toList();

        // 各知识点掌握情况
        List<Map<String, Object>> knowledgeStats = statsList.stream()
                .filter(s -> s.getTotalCount() > 0)
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("knowledgePoint", s.getKnowledgePoint());
                    m.put("accuracy", s.getAccuracy());
                    m.put("totalCount", s.getTotalCount());
                    m.put("correctCount", s.getCorrectCount());
                    m.put("wrongCount", s.getWrongCount());
                    return m;
                })
                .toList();

        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalQuestions", totalCount);
        overview.put("correctCount", correctCount);
        overview.put("wrongCount", wrongCount);
        overview.put("overallAccuracy", totalCount > 0
                ? Math.round(correctCount * 100.0 / totalCount) / 100.0 : 0);
        overview.put("knowledgePoints", knowledgeStats);
        overview.put("weakPoints", weakPoints);

        return overview;
    }

    @Override
    @Transactional
    public void updateStats(Long userId, String knowledgePoint, boolean isCorrect) {
        Optional<LearningStats> existing = learningStatsMapper
                .findByUserIdAndKnowledgePoint(userId, knowledgePoint);

        LearningStats stats;
        if (existing.isPresent()) {
            stats = existing.get();
            stats.setTotalCount(stats.getTotalCount() + 1);
            if (isCorrect) {
                stats.setCorrectCount(stats.getCorrectCount() + 1);
            } else {
                stats.setWrongCount(stats.getWrongCount() + 1);
            }
        } else {
            stats = new LearningStats();
            stats.setUserId(userId);
            stats.setKnowledgePoint(knowledgePoint);
            stats.setTotalCount(1);
            if (isCorrect) {
                stats.setCorrectCount(1);
                stats.setWrongCount(0);
            } else {
                stats.setCorrectCount(0);
                stats.setWrongCount(1);
            }
        }

        // 更新正确率
        if (stats.getTotalCount() > 0) {
            double accuracy = stats.getCorrectCount() * 100.0 / stats.getTotalCount();
            stats.setAccuracy(Math.round(accuracy * 100.0) / 100.0);
        }

        saveOrUpdate(stats);
    }
}
