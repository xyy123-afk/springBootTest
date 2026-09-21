package com.fenglin.springboottest.dto;

/**
 * 勾选/取消待办请求体：{ username, done }
 */
public record TodoToggleRequest(String username, boolean done) {
}
