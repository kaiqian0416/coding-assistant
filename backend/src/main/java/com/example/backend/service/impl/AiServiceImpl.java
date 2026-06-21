package com.example.backend.service.impl;

import com.example.backend.dto.CodeReviewResult;
import com.example.backend.entity.Question;
import com.example.backend.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI 服务实现
 */
@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    @Value("${ai.api.model:glm-4-plus}")
    private String model;

    private final RestTemplate restTemplate;

    public AiServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String diagnose(String code, String errorInfo, Question question) {
        if (apiKey == null || apiKey.isEmpty()) {
            return mockDiagnose(code, errorInfo, question);
        }
        return callAiDiagnose(code, errorInfo, question);
    }

    @Override
    public String generateQuestion(String knowledgePoint, String difficulty, String topic) {
        if (apiKey == null || apiKey.isEmpty()) {
            return mockGenerateQuestion(knowledgePoint, difficulty, topic);
        }
        return callAiGenerateQuestion(knowledgePoint, difficulty, topic);
    }

    @Override
    public CodeReviewResult reviewCode(String code, Question question, boolean outputOk) {
        if (apiKey == null || apiKey.isEmpty()) {
            return mockReviewCode(code, question, outputOk);
        }
        return callAiReviewCode(code, question, outputOk);
    }

    // ==================== 真实 API 调用 ====================

    private String callAiDiagnose(String code, String errorInfo, Question question) {
        String prompt = String.format("""
                你是一位Python编程导师。用3-5句话简要分析以下代码错误：
                题目：%s
                代码：
                ```python
                %s
                ```
                错误：%s
                直接指出问题在哪行、怎么改，不要展开讲概念。""",
                question.getTitle(), code, errorInfo);
        return callDeepSeek(prompt);
    }

    private String callAiGenerateQuestion(String knowledgePoint, String difficulty, String topic) {
        String prompt = String.format("""
                生成一道Python编程题，返回JSON格式（不要多余内容）：
                知识点：%s，难度：%s，主题：%s

                {"title": "题目标题", "description": "题目描述", "input_description": "输入描述",
                 "output_description": "输出描述", "sample_input": "样例输入",
                 "sample_output": "样例输出", "reference_code": "Python参考代码"}
                """, knowledgePoint, difficulty, topic);
        return callDeepSeek(prompt);
    }

    private String callDeepSeek(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new LinkedHashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1200);

            log.info("===== 调用 AI API =====");
            log.info("URL: {}", apiUrl);
            log.info("Model: {}", model);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, request, Map.class);

            log.info("HTTP Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            if (response.getBody() != null) {
                // 输出完整响应帮助调试
                try {
                    log.info("Full response JSON: {}",
                            new com.fasterxml.jackson.databind.ObjectMapper()
                                    .writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(response.getBody()));
                } catch (Exception ignored) {}

                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    if (message != null && message.get("content") != null) {
                        String content = message.get("content");
                        log.info("AI 返回内容长度: {}", content.length());
                        return content;
                    }
                }
            }
            return "AI 服务响应异常，请稍后重试。";
        } catch (Exception e) {
            log.error("AI API 调用失败", e);
            return "AI 服务调用失败：" + e.getMessage();
        }
    }

    // ==================== 模拟模式 ====================

    private String mockDiagnose(String code, String errorInfo, Question question) {
        if (errorInfo == null) errorInfo = "";
        StringBuilder diagnosis = new StringBuilder();
        diagnosis.append("【AI 诊断建议】\n\n📝 错误分析：\n");
        if (errorInfo.contains("compile_error") || errorInfo.contains("语法")) {
            diagnosis.append("您的代码存在语法错误，请检查以下方面：\n");
            diagnosis.append("1. 检查所有括号是否匹配（{}、()）\n");
            diagnosis.append("2. 确保每条语句以分号(;)结尾\n");
            diagnosis.append("3. 检查变量类型声明是否正确\n");
        } else if (errorInfo.contains("期望输出")) {
            diagnosis.append("您的代码运行结果与预期不符。建议：\n");
            diagnosis.append("1. 仔细阅读题目要求，确认输出格式\n");
            diagnosis.append("2. 使用题目给出的样例输入进行测试\n");
            diagnosis.append("3. 检查边界条件是否处理正确\n");
        } else {
            diagnosis.append("代码运行出现异常。建议：\n");
            diagnosis.append("1. 检查数组下标是否越界\n");
            diagnosis.append("2. 确认输入读取方式是否正确\n");
            diagnosis.append("3. 检查是否存在除零等运行时错误\n");
        }
        diagnosis.append("\n💡 学习建议：\n");
        diagnosis.append("• 本题涉及知识点：").append(question.getKnowledgePoint()).append("\n");
        diagnosis.append("• 建议先理解题目中的算法逻辑，再动手编码\n");
        diagnosis.append("• 可以尝试在本地 IDE 中调试运行\n");
        return diagnosis.toString();
    }

    private String mockGenerateQuestion(String knowledgePoint, String difficulty, String topic) {
        String diffText = switch (difficulty) {
            case "easy" -> "简单";
            case "medium" -> "中等";
            case "hard" -> "困难";
            default -> "简单";
        };
        return String.format("""
                {"title": "%s", "description": "请编写一个Python程序，实现与「%s」相关的编程任务。难度：%s，知识点：%s。",
                 "input_description": "从标准输入读取数据。", "output_description": "向标准输出打印结果。",
                 "sample_input": "样例输入", "sample_output": "样例输出",
                 "reference_code": "# 在这里编写你的Python代码"}
                """, topic, topic, diffText, knowledgePoint);
    }

    // ==================== AI 代码审查 ====================

    private CodeReviewResult callAiReviewCode(String code, Question question, boolean outputOk) {
        String prompt = String.format("""
                判断以下 Python 代码是真正解题还是面向结果硬编码。
                题目：%s
                代码：
                ```python
                %s
                ```
                以 JSON 返回（仅返回JSON，不要多余文字）：
                {"isGenuineSolution": 是否真正解题而非硬编码, "score": 解题评分0-100, "feedback": "一句话评价"}
                """, question.getTitle(), code);
        String responseJson = callDeepSeek(prompt);
        return parseReviewResult(responseJson);
    }

    private CodeReviewResult parseReviewResult(String json) {
        CodeReviewResult result = new CodeReviewResult();
        try {
            String clean = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            int start = clean.indexOf('{');
            int end = clean.lastIndexOf('}');
            if (start >= 0 && end > start) {
                clean = clean.substring(start, end + 1);
            }
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(clean);
            result.setGenuineSolution(root.path("isGenuineSolution").asBoolean(true));
            result.setFeedback(root.path("feedback").asText(""));
        } catch (Exception e) {
            result.setGenuineSolution(false);
            result.setFeedback("⚠️ AI 审查异常，请确保代码真正实现了算法。");
        }
        return result;
    }

    private CodeReviewResult mockReviewCode(String code, Question question, boolean outputOk) {
        CodeReviewResult result = new CodeReviewResult();
        result.setGenuineSolution(true);
        result.setScore(outputOk ? 80 : 30);
        String codeLower = code.toLowerCase();
        boolean hasInput = codeLower.contains("input(") || codeLower.contains("sys.stdin");
        boolean hasVariables = code.contains("=");
        boolean purePrint = !hasInput && !hasVariables && codeLower.contains("print(");
        StringBuilder feedback = new StringBuilder();
        if (purePrint && outputOk) {
            result.setGenuineSolution(false);
            result.setScore(10);
            feedback.append("⚠️ 代码看起来只是打印了固定值，没有真正读取输入。");
        } else if (!hasInput) {
            result.setScore(outputOk ? 50 : 20);
            feedback.append("📌 代码中没有检测到 input() 调用。");
        } else if (!hasVariables) {
            result.setScore(outputOk ? 60 : 25);
            feedback.append("📌 代码逻辑较简单，尝试使用变量和表达式。");
        } else {
            feedback.append("✅ ");
            feedback.append(outputOk ? "代码正确读取输入并处理，解题思路正确！" : "输出与预期不符，请检查算法逻辑。");
        }
        result.setFeedback(feedback.toString());
        return result;
    }
}
