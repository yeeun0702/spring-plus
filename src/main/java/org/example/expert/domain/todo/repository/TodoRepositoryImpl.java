package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements CustomTodoRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin() // N+1 문제 방지
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }


    @Override
    public Page<TodoSearchResponse> searchTodos(TodoSearchRequest todoSearchRequest, Pageable pageable) {

        QTodo todo = QTodo.todo;
        QUser user = QUser.user;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        // BooleanExpression 조건들 정의
        BooleanExpression titleCondition = hasTitle(todoSearchRequest.getTitle(), todo);
        BooleanExpression nicknameCondition = hasManagerNickname(todoSearchRequest.getManagerNickname(), user);
        BooleanExpression fromCondition = createdAtFrom(todoSearchRequest.getFrom(), todo);
        BooleanExpression toCondition = createdAtTo(todoSearchRequest.getTo(), todo);

        // 메인 쿼리
        List<TodoSearchResponse> result = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.id.countDistinct(),   // 담당자 수
                        comment.id.countDistinct()    // 댓글 수
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(titleCondition, nicknameCondition, fromCondition, toCondition)
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long total = queryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(titleCondition, nicknameCondition, fromCondition, toCondition)
                .fetchOne();

        return new PageImpl<>(result, pageable, total != null ? total : 0L);
    }

    private BooleanExpression hasTitle(String title, QTodo todo) {
        return (title != null && !title.isBlank()) ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression hasManagerNickname(String nickname, QUser user) {
        return (nickname != null && !nickname.isBlank()) ? user.nickname.containsIgnoreCase(nickname) : null;
    }

    private BooleanExpression createdAtFrom(LocalDate from, QTodo todo) {
        return (from != null) ? todo.createdAt.goe(from.atStartOfDay()) : null;
    }

    private BooleanExpression createdAtTo(LocalDate to, QTodo todo) {
        return (to != null) ? todo.createdAt.loe(to.atTime(23, 59, 59)) : null;
    }

}
