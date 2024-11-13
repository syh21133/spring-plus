package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        Todo result = queryFactory
            .selectFrom(todo)
            .join(todo.user, user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> findTodoByKeyword(String title, String nickName,
        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        JPQLQuery<Long> commentsNum = JPAExpressions.select(comment.count())
            .from(comment)
            .where(comment.todo.id.eq(todo.id));

        JPQLQuery<Long> managersNum = JPAExpressions.select(manager.count())
            .from(manager)
            .where(manager.todo.id.eq(todo.id));

        List<TodoSearchResponse> results = queryFactory
            .select(Projections.constructor(
                TodoSearchResponse.class,
                todo.title,
                managersNum,
                commentsNum
            ))
            .from(todo)
            .leftJoin(todo.managers, manager)
            .where(
                title != null ? todo.title.containsIgnoreCase(title) : null,
                startDate != null && endDate != null ? todo.createdAt.between(startDate, endDate)
                    : null,
                nickName != null ? user.nickName.containsIgnoreCase(nickName) : null
            )
            .orderBy(todo.createdAt.desc())
            .fetch();

        long total = queryFactory
            .select(todo.count())
            .from(todo)
            .where(
                title != null ? todo.title.containsIgnoreCase(title) : null,
                startDate != null && endDate != null ? todo.createdAt.between(startDate, endDate)
                    : null,
                nickName != null ? user.nickName.containsIgnoreCase(nickName) : null
            )
            .fetchOne();

        return new PageImpl<>(results, pageable, total);

    }
}
