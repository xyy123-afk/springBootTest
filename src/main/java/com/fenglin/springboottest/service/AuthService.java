package com.fenglin.springboottest.service;

import com.fenglin.springboottest.common.ApiResponse;
import com.fenglin.springboottest.dto.UserVO;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    /** BCrypt 每次加密都会生成随机盐，同一个密码两次密文不同，校验只能用 matches() */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 注册：后端兜底校验（前端已校验过一遍），密码 BCrypt 加密后落库 */
    public ApiResponse<UserVO> register(String username, String password) {
        if (username == null || username.isBlank()) {
            return ApiResponse.fail("请输入用户名或邮箱", "username");
        }
        username = username.trim();
        if (username.length() < 2) {
            return ApiResponse.fail("用户名至少 2 个字符", "username");
        }
        if (username.length() > 32) {
            return ApiResponse.fail("用户名不能超过 32 个字符", "username");
        }
        if (password == null || password.isBlank()) {
            return ApiResponse.fail("请输入密码", "password");
        }
        if (password.length() < 6) {
            return ApiResponse.fail("密码长度至少 6 位", "password");
        }
        if (password.length() > 72) {
            // BCrypt 只取前 72 字节，超长直接拒绝更稳妥
            return ApiResponse.fail("密码长度不能超过 72 位", "password");
        }
        if (userRepository.existsByUsername(username)) {
            return ApiResponse.fail("该用户名已被注册", "username");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // 极端并发下两人同时注册同一用户名，数据库唯一索引是最后一道防线
            return ApiResponse.fail("该用户名已被注册", "username");
        }
        return ApiResponse.ok(new UserVO(user.getUsername()));
    }

    /** 登录：校验 BCrypt 密文；不区分「用户不存在」和「密码错误」，避免被枚举用户名 */
    public ApiResponse<UserVO> login(String username, String password) {
        if (username == null || username.isBlank()) {
            return ApiResponse.fail("请输入用户名或邮箱", "username");
        }
        if (password == null || password.isBlank()) {
            return ApiResponse.fail("请输入密码", "password");
        }

        User user = userRepository.findByUsername(username.trim()).orElse(null);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return ApiResponse.fail("用户名或密码错误", "");
        }
        return ApiResponse.ok(new UserVO(user.getUsername()));
    }
}
