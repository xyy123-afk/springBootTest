package com.fenglin.springboottest.dto;

/**
 * 新增待办请求体：{ username, content }
 */
public record TodoAddRequest(String username, String content) {
}
