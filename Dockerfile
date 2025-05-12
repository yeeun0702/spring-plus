# Gradle로 빌드 (테스트 생략)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# 실행 이미지 (경량화)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
