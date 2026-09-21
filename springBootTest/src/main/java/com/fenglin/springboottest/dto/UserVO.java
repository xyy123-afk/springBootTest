package com.fenglin.springboottest.dto;

/**
 * 返回给前端的用户信息，只暴露用户名，绝不能把密码字段带出去。
 */
public record UserVO(String username) {
}
