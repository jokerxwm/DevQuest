package com.devquest.common.config;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/admin",
            "/api/search",
            "/api/tags",
            "/api/stats"
    );

    private static final List<String> PUBLIC_GET_PREFIXES = Arrays.asList(
            "/api/questions",
            "/api/answers/question",
            "/api/comments"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String path = request.getRequestURI();
                String method = request.getMethod();

                if (isWhiteListed(path) || isPublicGet(path, method)) {
                    return true;
                }

                StpUtil.checkLogin();
                return true;
            }
        }).addPathPatterns("/api/**");
    }

    private boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    private boolean isPublicGet(String path, String method) {
        return "GET".equalsIgnoreCase(method)
                && PUBLIC_GET_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
