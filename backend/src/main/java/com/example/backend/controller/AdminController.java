package com.example.backend.controller;

import com.example.backend.config.SessionInterceptor;
import com.example.backend.dto.AiGenerateDTO;
import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.Achievement;
import com.example.backend.entity.Question;
import com.example.backend.entity.User;
import com.example.backend.mapper.AchievementMapper;
import com.example.backend.mapper.SubmissionMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final QuestionService questionService;
    private final SubmissionMapper submissionMapper;
    private final UserMapper userMapper;
    private final AchievementMapper achievementMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminController(QuestionService questionService, SubmissionMapper submissionMapper,
                           UserMapper userMapper, AchievementMapper achievementMapper,
                           PasswordEncoder passwordEncoder) {
        this.questionService = questionService;
        this.submissionMapper = submissionMapper;
        this.userMapper = userMapper;
        this.achievementMapper = achievementMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== AI 出题 ====================

    @PostMapping("/ai/generate-question")
    public ApiResponse<Question> generateQuestion(@Valid @RequestBody AiGenerateDTO dto, HttpServletRequest request) {
        User admin = requireAdmin(request);
        if (admin == null) return ApiResponse.error("权限不足");
        try { return ApiResponse.success("题目生成成功", questionService.generateByAI(dto, admin.getId()));
        } catch (Exception e) { return ApiResponse.error("生成失败：" + e.getMessage()); }
    }

    // ==================== 题目 CRUD ====================

    @PostMapping("/questions")
    public ApiResponse<Question> addQuestion(@RequestBody Question question, HttpServletRequest request) {
        User admin = requireAdmin(request);
        if (admin == null) return ApiResponse.error("权限不足");
        question.setSource("manual"); question.setReviewStatus("approved"); question.setCreatedBy(admin.getId());
        questionService.save(question);
        return ApiResponse.success("题目添加成功", question);
    }

    @PutMapping("/questions/{id}")
    public ApiResponse<Question> updateQuestion(@PathVariable Long id, @RequestBody Question updateData) {
        Question q = questionService.getById(id);
        if (q == null) return ApiResponse.error("题目不存在");
        if (updateData.getTitle() != null) q.setTitle(updateData.getTitle());
        if (updateData.getDescription() != null) q.setDescription(updateData.getDescription());
        if (updateData.getDifficulty() != null) q.setDifficulty(updateData.getDifficulty());
        if (updateData.getKnowledgePoint() != null) q.setKnowledgePoint(updateData.getKnowledgePoint());
        if (updateData.getSampleInput() != null) q.setSampleInput(updateData.getSampleInput());
        if (updateData.getSampleOutput() != null) q.setSampleOutput(updateData.getSampleOutput());
        if (updateData.getReferenceCode() != null) q.setReferenceCode(updateData.getReferenceCode());
        questionService.updateById(q);
        return ApiResponse.success("更新成功", q);
    }

    @DeleteMapping("/questions/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        questionService.removeById(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 审核 ====================

    @GetMapping("/questions/pending-review")
    public ApiResponse<List<Question>> getPendingReview(HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        return ApiResponse.success(questionService.getPendingReviewQuestions());
    }

    @PutMapping("/questions/{id}/approve")
    public ApiResponse<Void> approveQuestion(@PathVariable Long id, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        questionService.approveQuestion(id);
        return ApiResponse.success("审核通过", null);
    }

    @PutMapping("/questions/{id}/reject")
    public ApiResponse<Void> rejectQuestion(@PathVariable Long id, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        questionService.rejectQuestion(id);
        return ApiResponse.success("已驳回", null);
    }

    // ==================== 提交记录 ====================

    @GetMapping("/submissions")
    public ApiResponse<List<Map<String, Object>>> getAllSubmissions(HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        return ApiResponse.success(submissionMapper.findAllWithUsername());
    }

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public ApiResponse<List<User>> listUsers(HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        List<User> users = userMapper.selectList(null);
        users.forEach(u -> u.setPassword(null));
        return ApiResponse.success(users);
    }

    @PostMapping("/users")
    public ApiResponse<Map<String, Object>> addUser(@RequestBody Map<String, String> body, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        String username = body.get("username"), password = body.get("password");
        if (username == null || password == null || username.isBlank() || password.length() < 6)
            return ApiResponse.error("用户名不能为空，密码至少6个字符");
        if (userMapper.findByUsername(username).isPresent()) return ApiResponse.error("用户名已存在");
        User user = new User();
        user.setUsername(username); user.setPassword(passwordEncoder.encode(password));
        user.setNickname(body.getOrDefault("nickname", username));
        String email = body.get("email"); user.setEmail(email != null && !email.isBlank() ? email : null); user.setRole(body.getOrDefault("role", "user"));
        user.setAbilityLevel(body.getOrDefault("abilityLevel", "easy"));
        userMapper.insert(user); user.setPassword(null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId()); result.put("username", user.getUsername());
        result.put("nickname", user.getNickname()); result.put("role", user.getRole());
        return ApiResponse.success("用户创建成功", result);
    }

    @PutMapping("/users/{id}")
    public ApiResponse<Void> updateUser(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.error("用户不存在");
        if (body.containsKey("nickname")) user.setNickname(body.get("nickname"));
        if (body.containsKey("email")) { String e = body.get("email"); user.setEmail(e != null && !e.isBlank() ? e : null); }
        if (body.containsKey("role")) user.setRole(body.get("role"));
        if (body.containsKey("abilityLevel")) user.setAbilityLevel(body.get("abilityLevel"));
        if (body.containsKey("password") && !body.get("password").isBlank()) user.setPassword(passwordEncoder.encode(body.get("password")));
        userMapper.updateById(user);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        User admin = requireAdmin(request);
        if (admin.getId().equals(id)) return ApiResponse.error("不能删除自己");
        userMapper.deleteById(id);
        return ApiResponse.success("已删除", null);
    }

    // ==================== 成就管理 ====================

    @GetMapping("/achievements")
    public ApiResponse<List<Achievement>> listAchievements(HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        return ApiResponse.success(achievementMapper.selectList(null));
    }

    @PostMapping("/achievements")
    public ApiResponse<Void> addAchievement(@RequestBody Achievement achievement, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        achievement.setId(null);
        achievementMapper.insert(achievement);
        return ApiResponse.success("成就已创建", null);
    }

    @PutMapping("/achievements/{id}")
    public ApiResponse<Void> updateAchievement(@PathVariable Long id, @RequestBody Achievement data, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        Achievement a = achievementMapper.selectById(id);
        if (a == null) return ApiResponse.error("成就不存在");
        if (data.getName() != null) a.setName(data.getName());
        if (data.getDescription() != null) a.setDescription(data.getDescription());
        if (data.getIcon() != null) a.setIcon(data.getIcon());
        if (data.getConditionType() != null) a.setConditionType(data.getConditionType());
        if (data.getConditionValue() != null) a.setConditionValue(data.getConditionValue());
        if (data.getSortOrder() != null) a.setSortOrder(data.getSortOrder());
        achievementMapper.updateById(a);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/achievements/{id}")
    public ApiResponse<Void> deleteAchievement(@PathVariable Long id, HttpServletRequest request) {
        if (requireAdmin(request) == null) return ApiResponse.error("权限不足");
        achievementMapper.deleteById(id);
        return ApiResponse.success("已删除", null);
    }

    // ==================== 权限校验 ====================

    private User requireAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        User user = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        return (user != null && "admin".equals(user.getRole())) ? user : null;
    }
}
