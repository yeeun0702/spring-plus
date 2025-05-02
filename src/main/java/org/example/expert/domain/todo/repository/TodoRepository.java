package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TodoRepository extends JpaRepository<Todo, Long>, CustomTodoRepository {

    @Query("""
    SELECT t FROM Todo t
    WHERE (:weather IS NULL OR t.weather = :weather)
    AND (:startDate IS NULL OR :endDate IS NULL OR t.modifiedAt BETWEEN :startDate AND :endDate)
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchTodosWithConditions(
            @Param("weather") String weather,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
