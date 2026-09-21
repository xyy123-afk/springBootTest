package com.fenglin.springboottest.dto;

/**
 * 返回给前端的待办项。
 */
public record TodoVO(Long id, String content, boolean done) {
}
