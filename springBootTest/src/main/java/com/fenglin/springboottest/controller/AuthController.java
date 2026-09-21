package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.ApiResponse;
import com.fenglin.springboottest.dto.AuthRequest;
import com.fenglin.springboottest.dto.UserVO;
import com.fenglin.springboottest.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 * 前端统一用相对路径 /api/auth/xxx 发 POST JSON，开发环境由 Vite 代理转到 8081，
 * 所以后端不需要配 CORS。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 注册：请求体 { username, password } */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@RequestBody AuthRequest req) {
        return authService.register(req.username(), req.password());
    }

    /** 登录：请求体 { username, password } */
    @PostMapping("/login")
    public ApiResponse<UserVO> login(@RequestBody AuthRequest req) {
        return authService.login(req.username(), req.password());
    }
}
