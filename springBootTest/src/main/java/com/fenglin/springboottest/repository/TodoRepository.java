package com.fenglin.springboottest.repository;

import com.fenglin.springboottest.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    /** 某用户的待办列表，新的在前 */
    List<Todo> findByUserIdOrderByIdDesc(Long userId);

    /** 按 id + 用户 id 双条件查，保证只能操作自己的待办 */
    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
