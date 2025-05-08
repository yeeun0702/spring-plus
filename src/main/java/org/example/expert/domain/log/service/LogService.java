package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.enums.LogStatus;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    // 로그 시작 -> 기존 트랜잭션 무시하고, 새 트랜잭션 실행
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Log startLog(String message) {
        Log log = Log.builder()
                .message(message)
                .createdAt(LocalDateTime.now())
                .status(LogStatus.IN_PROGRESS)
                .build();

        return logRepository.save(log);
    }

    // 로그 상태 업데이트
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateLogStatus(Long logId, LogStatus status) {
        Log log = logRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Log not found"));
        log.changeStatus(status);
    }
}