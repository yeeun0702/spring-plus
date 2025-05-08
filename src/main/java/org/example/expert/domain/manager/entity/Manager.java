package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "managers")
public class Manager {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // 일정 만든 사람 id
    private User user; // 매니저 역할을 하는 유저

    @ManyToOne(fetch = FetchType.LAZY) // 일정 id
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo; // 어떤 일정에 매니저로 등록됐는지

    @Builder
    public Manager(User user, Todo todo) {
        this.user = user;
        this.todo = todo;
    }
}
