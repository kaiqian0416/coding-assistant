package com.example.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dto.CodeReviewResult;
import com.example.backend.dto.SubmissionDTO;
import com.example.backend.entity.Question;
import com.example.backend.entity.Submission;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.mapper.SubmissionMapper;
import com.example.backend.service.AiService;
import com.example.backend.service.LearningStatsService;
import com.example.backend.service.SubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {

    private final SubmissionMapper submissionMapper;
    private final QuestionMapper questionMapper;
    private final AiService aiService;
    private final LearningStatsService learningStatsService;

    // 保存编译错误的关键词列表
    private static final List<String> COMPILE_ERROR_KEYWORDS = List.of(
            "error:", "cannot find symbol", "incompatible types",
            " expected", "missing return", "illegal start of"
    );

    public SubmissionServiceImpl(SubmissionMapper submissionMapper,
                                 QuestionMapper questionMapper,
                                 AiService aiService,
                                 LearningStatsService learningStatsService) {
        this.submissionMapper = submissionMapper;
        this.questionMapper = questionMapper;
        this.aiService = aiService;
        this.learningStatsService = learningStatsService;
    }

    @Override
    @Transactional
    public Submission submitCode(Long userId, SubmissionDTO dto) {
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) throw new RuntimeException("题目不存在");

        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setQuestionId(dto.getQuestionId());
        submission.setCode(dto.getCode());
        submission.setLanguage(dto.getLanguage() != null ? dto.getLanguage() : "python");
        submission.setResult("judging");
        submission.setScore(0);
        save(submission);

        // 判题
        JudgeResult judgeResult = judge(submission.getCode(), question);
        submission.setResult(judgeResult.result);
        submission.setScore(judgeResult.score);
        submission.setErrorInfo(judgeResult.errorInfo);

        // AI 代码审查（防面向结果编程）
        boolean outputOk = "accepted".equals(judgeResult.result);
        CodeReviewResult review = aiService.reviewCode(submission.getCode(), question, outputOk);

        // 只有检测到纯硬编码时才降分，否则平台分数说了算
        if (!review.isGenuineSolution() && outputOk) {
            submission.setResult("wrong_answer");
            submission.setScore(Math.min(submission.getScore(), 15));
        }

        // 组装诊断 + 审查反馈
        StringBuilder sb = new StringBuilder();
        if (!outputOk) {
            String diagnosis = aiService.diagnose(submission.getCode(), judgeResult.errorInfo, question);
            sb.append("【错误诊断】\n").append(diagnosis).append("\n\n");
        }
        sb.append("【AI 代码审查】\n");
        if (review.getFeedback() != null) sb.append(review.getFeedback()).append("\n");
        if (!review.isGenuineSolution() && outputOk) {
            sb.append("⚠️ 注意：输出虽然正确，但代码像是直接打印固定值。请用 input() 读取输入后编写通用解法。");
        }
        submission.setAiDiagnosis(sb.toString());
        updateById(submission);

        // 更新学习统计
        learningStatsService.updateStats(userId, question.getKnowledgePoint(),
                outputOk && review.isGenuineSolution());
        return submission;
    }

    /**
     * 判题核心逻辑
     * 优先使用真实 Python 执行，失败时回退到模拟判题。
     */
    private JudgeResult judge(String code, Question question) {
        JudgeResult result = realRunPython(code, question);
        if (result != null) return result;
        return simulateJudge(code, question);
    }

    /**
     * 模式1：模拟判题（Python）
     * 通过分析代码中的 print() 输出与预期输出对比
     */
    private JudgeResult simulateJudge(String code, Question question) {
        JudgeResult result = new JudgeResult();

        if (code == null || code.trim().isEmpty()) {
            result.result = "compile_error"; result.score = 0;
            result.errorInfo = "代码不能为空"; return result;
        }

        if (hasObviousSyntaxError(code)) {
            result.result = "compile_error"; result.score = 0;
            result.errorInfo = "语法错误：请检查括号、引号是否匹配。"; return result;
        }

        String userOutput = extractPythonOutput(code);
        String expectedOutput = question.getSampleOutput();
        if (expectedOutput == null) expectedOutput = "";

        if (compareOutput(userOutput, expectedOutput)) {
            result.result = "accepted"; result.score = 100; result.errorInfo = null;
        } else {
            result.result = "wrong_answer"; result.score = 30;
            result.errorInfo = String.format("期望输出：\n%s\n\n你的输出：\n%s",
                    expectedOutput.trim(), userOutput.trim());
        }
        return result;
    }

    /**
     * 检查明显的语法错误（适用于 Python）
     */
    private boolean hasObviousSyntaxError(String code) {
        int parenCount = 0, bracketCount = 0, braceCount = 0;
        for (char c : code.toCharArray()) {
            if (c == '(') parenCount++; if (c == ')') parenCount--;
            if (c == '[') bracketCount++; if (c == ']') bracketCount--;
            if (c == '{') braceCount++; if (c == '}') braceCount--;
        }
        return parenCount != 0 || bracketCount != 0 || braceCount != 0;
    }

    /**
     * 从 Python 代码中提取 print() 输出的内容
     */
    private String extractPythonOutput(String code) {
        StringBuilder output = new StringBuilder();
        Pattern pattern = Pattern.compile("print\\s*\\(([^)]*)\\)");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String arg = matcher.group(1).trim();
            if (arg.isEmpty()) {
                output.append("\n");
            } else if ((arg.startsWith("\"") && arg.endsWith("\""))
                    || (arg.startsWith("'") && arg.endsWith("'"))) {
                output.append(processEscapeChars(arg.substring(1, arg.length() - 1)));
            } else if (arg.contains(",")) {
                // 多参数，用空格分隔
                String[] parts = arg.split(",");
                for (int i = 0; i < parts.length; i++) {
                    String p = parts[i].trim();
                    if (p.startsWith("end=") || p.startsWith("sep=")) continue;
                    if (i > 0) output.append(" ");
                    if ((p.startsWith("\"") && p.endsWith("\""))
                            || (p.startsWith("'") && p.endsWith("'"))) {
                        output.append(p.substring(1, p.length() - 1));
                    } else if (p.equals("True") || p.equals("False")) {
                        output.append(p.toLowerCase());
                    } else if (p.matches("-?\\d+(\\.\\d+)?")) {
                        output.append(p);
                    } else {
                        output.append("<变量输出>");
                    }
                }
            } else {
                String t = arg.trim();
                if (t.equals("True") || t.equals("False")) output.append(t.toLowerCase());
                else if (t.matches("-?\\d+(\\.\\d+)?")) output.append(t);
                else output.append("<变量输出>");
            }
        }
        return output.toString().trim();
    }

    private String processEscapeChars(String s) {
        return s.replace("\\t", "\t")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\"", "\"")
                .replace("\\'", "'");
    }

    /**
     * 比较用户输出与预期输出
     */
    private boolean compareOutput(String userOutput, String expectedOutput) {
        if (userOutput == null && expectedOutput == null) return true;
        if (userOutput == null || expectedOutput == null) return false;

        // 去除两端空白后比较
        String userTrimmed = userOutput.trim().replaceAll("\\s+", " ");
        String expectedTrimmed = expectedOutput.trim().replaceAll("\\s+", " ");

        return userTrimmed.equals(expectedTrimmed);
    }

    /**
     * 模式2：真实 Python 执行
     * 使用系统 Python 解释器直接运行用户代码，捕获 stdout 与预期输出对比。
     *
     * @return 判题结果，若 Python 不可用则返回 null
     */
    private JudgeResult realRunPython(String code, Question question) {
        if (code == null || code.trim().isEmpty()) {
            JudgeResult r = new JudgeResult();
            r.result = "compile_error"; r.score = 0;
            r.errorInfo = "代码不能为空"; return r;
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("judge_", ".py");
            try (FileWriter fw = new FileWriter(tempFile)) {
                fw.write(code);
            }

            String sampleInput = question.getSampleInput();
            String output = runPythonCode(tempFile, sampleInput);

            JudgeResult result = new JudgeResult();
            String expectedOutput = question.getSampleOutput();

            if (compareOutput(output, expectedOutput)) {
                result.result = "accepted"; result.score = 100; result.errorInfo = null;
            } else {
                result.result = "wrong_answer";
                result.score = sampleOutputMatchScore(output, expectedOutput);
                result.errorInfo = String.format("期望输出：\n%s\n\n实际输出：\n%s",
                        expectedOutput != null ? expectedOutput.trim() : "",
                        output != null ? output.trim() : "");
            }
            tempFile.delete();
            return result;

        } catch (Exception e) {
            if (tempFile != null) tempFile.delete();
            return null;
        }
    }

    /**
     * 运行 Python 代码并捕获输出
     */
    private String runPythonCode(File scriptFile, String input) throws Exception {
        // 尝试查找可用的 Python 命令
        String[] pythonCmds = {"python3", "python"};
        String pythonBin = "python";
        for (String cmd : pythonCmds) {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{cmd, "--version"});
                if (p.waitFor() == 0) { pythonBin = cmd; break; }
            } catch (Exception ignored) {}
        }

        ProcessBuilder pb = new ProcessBuilder(pythonBin, scriptFile.getAbsolutePath());
        pb.redirectErrorStream(true);

        Process process = pb.start();

        if (input != null && !input.isEmpty()) {
            try (OutputStream os = process.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        try (InputStream is = process.getInputStream()) {
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
        }

        int exitCode = process.waitFor();
        String output = baos.toString().trim();

        if (exitCode != 0 && output.toLowerCase().contains("error")) {
            throw new RuntimeException("Python 执行错误:\n" + output);
        }

        return output;
    }

    private int sampleOutputMatchScore(String actual, String expected) {
        if (actual == null || expected == null) return 0;
        String a = actual.trim();
        String e = expected.trim();
        if (a.isEmpty() && e.isEmpty()) return 100;
        if (a.equals(e)) return 100;
        if (a.contains(e) || e.contains(a)) return 50;
        return 30;
    }

    @Override
    public List<Submission> getUserSubmissions(Long userId) {
        return submissionMapper.findByUserId(userId);
    }

    @Override
    public List<Submission> getQuestionSubmissions(Long questionId) {
        return submissionMapper.findByQuestionId(questionId);
    }

    @Override
    public List<Submission> getUserQuestionSubmissions(Long userId, Long questionId) {
        return submissionMapper.findByUserIdAndQuestionId(userId, questionId);
    }

    @Override
    public Map<String, Object> getUserSubmissionStats(Long userId) {
        List<Submission> submissions = submissionMapper.findByUserId(userId);

        int total = submissions.size();
        long accepted = submissions.stream().filter(s -> "accepted".equals(s.getResult())).count();
        long wrongAnswer = submissions.stream().filter(s -> "wrong_answer".equals(s.getResult())).count();
        long compileError = submissions.stream().filter(s -> "compile_error".equals(s.getResult())).count();
        long runtimeError = submissions.stream().filter(s -> "runtime_error".equals(s.getResult())).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total);
        stats.put("accepted", accepted);
        stats.put("wrongAnswer", wrongAnswer);
        stats.put("compileError", compileError);
        stats.put("runtimeError", runtimeError);
        stats.put("accuracy", total > 0 ? Math.round(accepted * 100.0 / total) / 100.0 : 0);

        return stats;
    }

    /**
     * 判题结果内部类
     */
    private static class JudgeResult {
        String result;
        int score;
        String errorInfo;
    }
}
