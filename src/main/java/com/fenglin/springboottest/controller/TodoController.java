package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.ApiResponse;
import com.fenglin.springboottest.dto.TodoAddRequest;
import com.fenglin.springboottest.dto.TodoToggleRequest;
import com.fenglin.springboottest.dto.TodoVO;
import com.fenglin.springboottest.entity.Todo;
import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.repository.TodoRepository;
import com.fenglin.springboottest.repository.UserRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 待办事项接口：/api/todos 的增删改查。
 * 演示项目简化：没有引入会话/token，前端登录后把 username 随请求带来标识"我是谁"。
 * 正式项目应换成 JWT 或服务端会话，再做用户校验。
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoController(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    /** 待办列表：GET /api/todos?username=xxx */
    @GetMapping
    public ApiResponse<List<TodoVO>> list(@RequestParam String username) {
        User user = findUser(username);
        if (user == null) {
            return ApiResponse.fail("用户未找到", "");
        }
        List<TodoVO> data = todoRepository.findByUserIdOrderByIdDesc(user.getId()).stream()
                .map(TodoController::toVO)
                .toList();
        return ApiResponse.ok(data);
    }

    /** 新增待办：POST /api/todos，请求体 { username, content } */
    @PostMapping
    public ApiResponse<TodoVO> add(@RequestBody TodoAddRequest req) {
        User user = findUser(req.username());
        if (user == null) {
            return ApiResponse.fail("用户未找到", "");
        }
        String content = req.content() == null ? "" : req.content().trim();
        if (content.isEmpty()) {
            return ApiResponse.fail("待办内容不能为空", "content");
        }
        if (content.length() > 255) {
            return ApiResponse.fail("待办内容太长了（最多 255 字）", "content");
        }
        Todo todo = new Todo();
        todo.setUserId(user.getId());
        todo.setContent(content);
        todoRepository.save(todo);
        return ApiResponse.ok(toVO(todo));
    }

    /** 勾选/取消待办：PUT /api/todos/{id}，请求体 { username, done } */
    @PutMapping("/{id}")
    public ApiResponse<TodoVO> toggle(@PathVariable Long id, @RequestBody TodoToggleRequest req) {
        Todo todo = findOwned(id, req.username());
        if (todo == null) {
            return ApiResponse.fail("待办不存在", "");
        }
        todo.setDone(req.done());
        todoRepository.save(todo);
        return ApiResponse.ok(toVO(todo));
    }

    /** 删除待办：DELETE /api/todos/{id}?username=xxx */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestParam String username) {
        Todo todo = findOwned(id, username);
        if (todo == null) {
            return ApiResponse.fail("待办不存在", "");
        }
        todoRepository.delete(todo);
        return ApiResponse.ok(null);
    }

    private User findUser(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userRepository.findByUsername(username.trim()).orElse(null);
    }

    /** 只能操作自己的待办：id + username 双重校验 */
    private Todo findOwned(Long id, String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return userRepository.findByUsername(username.trim())
                .flatMap(user -> todoRepository.findByIdAndUserId(id, user.getId()))
                .orElse(null);
    }

    private static TodoVO toVO(Todo t) {
        return new TodoVO(t.getId(), t.getContent(), t.isDone());
    }
}
