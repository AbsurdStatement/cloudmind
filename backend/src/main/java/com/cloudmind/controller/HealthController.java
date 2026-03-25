package com.cloudmind.controller;

import com.cloudmind.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查接口
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of("status", "UP", "service", "CloudMind Backend"));
    }

    @GetMapping("/me")
    public Result<Map<String, String>> me(jakarta.servlet.http.HttpServletRequest request) {
        return Result.ok(Map.of(
                "userId", String.valueOf(request.getAttribute("userId")),
                "username", String.valueOf(request.getAttribute("username"))
        ));
    }
}
