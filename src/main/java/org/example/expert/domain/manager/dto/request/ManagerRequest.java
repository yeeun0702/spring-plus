package org.example.expert.domain.manager.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManagerRequest {
    private Long userId;  // 매니저로 등록할 User의 id
    private Long todoId;  // 등록할 일정의 id

}
