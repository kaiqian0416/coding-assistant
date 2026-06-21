package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dto.AiGenerateDTO;
import com.example.backend.entity.LearningStats;
import com.example.backend.entity.Question;
import com.example.backend.entity.User;
import com.example.backend.mapper.LearningStatsMapper;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AiService;
import com.example.backend.service.QuestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final QuestionMapper questionMapper;
    private final LearningStatsMapper learningStatsMapper;
    private final UserMapper userMapper;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper,
                               LearningStatsMapper learningStatsMapper,
                               UserMapper userMapper,
                               AiService aiService,
                               ObjectMapper objectMapper) {
        this.questionMapper = questionMapper;
        this.learningStatsMapper = learningStatsMapper;
        this.userMapper = userMapper;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Question> getApprovedQuestions(String difficulty, String knowledgePoint) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getReviewStatus, "approved")
                .orderByAsc(Question::getDifficulty);

        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (knowledgePoint != null && !knowledgePoint.isEmpty()) {
            wrapper.eq(Question::getKnowledgePoint, knowledgePoint);
        }

        return list(wrapper);
    }

    @Override
    public List<Question> getPendingReviewQuestions() {
        return list(new LambdaQueryWrapper<Question>()
                .eq(Question::getReviewStatus, "pending")
                .orderByAsc(Question::getCreatedAt));
    }

    @Override
    @Transactional
    public void approveQuestion(Long questionId) {
        Question question = getById(questionId);
        if (question != null) {
            question.setReviewStatus("approved");
            updateById(question);
        }
    }

    @Override
    @Transactional
    public void rejectQuestion(Long questionId) {
        Question question = getById(questionId);
        if (question != null) {
            question.setReviewStatus("rejected");
            updateById(question);
        }
    }

    @Override
    @Transactional
    public Question generateByAI(AiGenerateDTO dto, Long adminId) {
        String aiResponse = aiService.generateQuestion(
                dto.getKnowledgePoint(), dto.getDifficulty(), dto.getTopic());

        // 打印 AI 原始返回，帮助调试
        log.info("===== AI 生成题目的原始返回 =====");
        log.info(aiResponse);
        log.info("================================");

        Question question = parseAiResponse(aiResponse, dto, adminId);
        save(question);
        return question;
    }

    private Question parseAiResponse(String json, AiGenerateDTO dto, Long adminId) {
        Question question = new Question();
        question.setSource("ai_generated");
        question.setReviewStatus("pending");
        question.setDifficulty(dto.getDifficulty());
        question.setKnowledgePoint(dto.getKnowledgePoint());
        question.setCreatedBy(adminId);

        try {
            String clean = json.replaceAll("```json\\s*", "")
                               .replaceAll("```\\s*", "").trim();
            int start = clean.indexOf('{');
            int end = clean.lastIndexOf('}');
            if (start >= 0 && end > start) {
                clean = clean.substring(start, end + 1);
            }
            JsonNode root = objectMapper.readTree(clean);
            question.setTitle(getJsonText(root, "title", dto.getTopic()));
            question.setDescription(getJsonText(root, "description", ""));
            question.setInputDescription(getJsonText(root, "input_description", ""));
            question.setOutputDescription(getJsonText(root, "output_description", ""));
            question.setSampleInput(getJsonText(root, "sample_input", ""));
            question.setSampleOutput(getJsonText(root, "sample_output", ""));
            question.setReferenceCode(getJsonText(root, "reference_code", ""));
        } catch (Exception e) {
            log.warn("AI 返回解析失败: {}", e.getMessage());
            question.setTitle(dto.getTopic());
            question.setDescription("AI 返回内容解析失败，请重试或手动录入。");
        }

        return question;
    }

    private String getJsonText(JsonNode root, String field, String defaultValue) {
        JsonNode node = root.get(field);
        if (node != null) return node.asText(defaultValue);
        // 也支持下划线和驼峰两种命名
        String camel = toCamelCase(field);
        if (!camel.equals(field)) {
            node = root.get(camel);
            if (node != null) return node.asText(defaultValue);
        }
        return defaultValue;
    }

    private String toCamelCase(String snake) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : snake.toCharArray()) {
            if (c == '_') { nextUpper = true; continue; }
            sb.append(nextUpper ? Character.toUpperCase(c) : c);
            nextUpper = false;
        }
        return sb.toString();
    }

    @Override
    public List<Question> getRecommendedQuestions(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return Collections.emptyList();

        List<LearningStats> weakestStats = learningStatsMapper.findWeakestKnowledgePoints(userId, 3);

        List<String> weakPoints;
        if (weakestStats.isEmpty()) {
            weakPoints = Collections.emptyList();
        } else {
            weakPoints = weakestStats.stream()
                    .map(LearningStats::getKnowledgePoint)
                    .collect(Collectors.toList());
        }

        String difficulty = user.getAbilityLevel();
        if (difficulty == null) difficulty = "easy";

        List<Question> recommended = new ArrayList<>();
        if (!weakPoints.isEmpty()) {
            List<Question> byWeakness = questionMapper.findRecommendedQuestions(weakPoints, difficulty, 5);
            if (byWeakness != null) recommended.addAll(byWeakness);
        }

        if (recommended.size() < 5) {
            List<Question> randomOnes = questionMapper.findRandomQuestions(5);
            if (randomOnes != null) {
                for (Question q : randomOnes) {
                    if (recommended.stream().noneMatch(r -> r.getId().equals(q.getId()))) {
                        recommended.add(q);
                    }
                }
            }
        }

        return recommended.size() > 5 ? recommended.subList(0, 5) : recommended;
    }
}
