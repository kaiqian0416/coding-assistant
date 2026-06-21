package com.example.backend.config;

import com.example.backend.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：校验用户是否已登录
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    public static final String SESSION_USER = "loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求（CORS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 放行登录、注册接口
        if (path.contains("/api/user/login") || path.contains("/api/user/register")) {
            return true;
        }

        // 题库 GET 请求公开访问（任何人都能看题目列表和详情）
        if (path.startsWith("/api/questions") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER) != null) {
            return true;
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
        return false;
    }
}
