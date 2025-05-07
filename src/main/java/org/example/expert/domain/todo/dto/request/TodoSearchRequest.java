package org.example.expert.domain.todo.dto.request;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TodoSearchRequest {
    private Integer page = 1;
    private Integer size = 10;
    private LocalDate from;
    private LocalDate to;
    private String title;
    private String managerNickname;
}