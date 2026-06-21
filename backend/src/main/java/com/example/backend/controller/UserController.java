package com.example.backend.controller;

import com.example.backend.config.SessionInterceptor;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ChangePasswordDTO;
import com.example.backend.dto.UserLoginDTO;
import com.example.backend.dto.UserRegisterDTO;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /** 注册 */
    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        try {
            User user = userService.register(dto);
            user.setPassword(null);
            return ApiResponse.success("注册成功", user);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /** 登录 */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto,
                                                   HttpServletRequest request) {
        try {
            User user = userService.login(dto);
            HttpSession session = request.getSession();
            session.setAttribute(SessionInterceptor.SESSION_USER, user);
            user.setPassword(null);
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("sessionId", session.getId());
            return ApiResponse.success("登录成功", data);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /** 登出 */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ApiResponse.success("已退出登录", null);
    }

    /** 获取个人信息 */
    @GetMapping("/profile")
    public ApiResponse<User> profile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        User user = userService.getById(sessionUser.getId());
        if (user != null) user.setPassword(null);
        return ApiResponse.success(user);
    }

    /** 更新资料 */
    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(@RequestBody User updateData,
                                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        User user = userService.getById(sessionUser.getId());
        if (user == null) return ApiResponse.error("用户不存在");

        if (updateData.getNickname() != null) user.setNickname(updateData.getNickname());
        if (updateData.getEmail() != null) user.setEmail(updateData.getEmail());
        if (updateData.getAvatar() != null) user.setAvatar(updateData.getAvatar());

        userService.updateById(user);
        session.setAttribute(SessionInterceptor.SESSION_USER, user);
        user.setPassword(null);
        return ApiResponse.success("更新成功", user);
    }

    /** 修改密码 */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto,
                                             HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute(SessionInterceptor.SESSION_USER);
        User user = userService.getById(sessionUser.getId());
        if (user == null) return ApiResponse.error("用户不存在");

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return ApiResponse.error("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.updateById(user);
        return ApiResponse.success("密码修改成功", null);
    }
}
