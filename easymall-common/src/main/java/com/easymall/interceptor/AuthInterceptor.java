package com.easymall.interceptor;

import com.easymall.component.RedisComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private RedisComponent redisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // ========== 管理员端验证 ==========
        if (uri.contains("/admin/")) {
            // 放行登录和验证码
            if (uri.contains("/admin/account/login") || uri.contains("/admin/account/checkCode")) {
                return true;
            }

            // 验证管理员 token
            String token = extractToken(request.getHeader("Authorization"));
            if (token == null) {
                return sendError(response, 401, "未登录，请先登录");
            }

            String account = redisComponent.getAdminInfoByToken(token);
            if (account == null) {
                return sendError(response, 401, "登录已过期，请重新登录");
            }

            request.setAttribute("adminAccount", account);
            return true;
        }

        // ========== 用户端验证 ==========
        // 放行公开接口
        if (isPublicApi(uri)) {
            return true;
        }

        // 验证用户 userId
        String userId = request.getHeader("userId");
        if (userId == null || userId.isEmpty()) {
            return sendError(response, 401, "未登录，请先登录");
        }

        request.setAttribute("userId", userId);
        return true;
    }

    private boolean isPublicApi(String uri) {
        String[] publicPaths = {
                "/auth/login", "/auth/checkCode",
                "/product/list", "/product/",
                "/category/tree", "/brand/enabled"
        };

        for (String path : publicPaths) {
            if (uri.contains(path)) {
                return true;
            }
        }
        return false;
    }

    private String extractToken(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }

    private boolean sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));

        return false;
    }
}