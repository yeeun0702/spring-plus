package org.example.expert.domain.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserInsertTest {

    @PersistenceContext
    private EntityManager entityManager;

    private static final int TOTAL_COUNT = 1_000_000;
    private static final int BATCH_SIZE = 1000;


    @Autowired
    private DataSource dataSource;

    @Test
    void printDataSourceInfo() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("연결된 DB URL: " + conn.getMetaData().getURL());
            System.out.println("사용자명: " + conn.getMetaData().getUserName());
        }
    }


    @Test
    @Transactional
    void insertMillionUsersWithJpa() {
        System.out.println("100만 건 유저 insert 시작");

        Set<String> nicknames = new HashSet<>();

        for (int i = 0; i < TOTAL_COUNT;) {
            String nickname = UUID.randomUUID().toString().substring(0, 10);
            if (!nicknames.add(nickname)) continue;

            String email = nickname + "@test.com";

            User user = User.builder()
                    .nickname(nickname)
                    .email(email)
                    .password("password")
                    .userRole(UserRole.USER)
                    .build();


            entityManager.persist(user);

            if (++i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
                System.out.println("▶ 진행 중: " + i + "명");
            }
        }

        entityManager.flush();
        entityManager.clear();

        System.out.println("100만 명 유저 insert 완료");
    }
}