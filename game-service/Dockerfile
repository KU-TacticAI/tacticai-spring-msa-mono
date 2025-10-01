# Stage 1: 빌드 환경 (Gradle 사용)
FROM gradle:8.4.0-jdk17 AS builder

WORKDIR /app

# 모노레포의 모든 소스코드 복사
COPY . .

# Game Service만 빌드합니다.
RUN ./gradlew :game-service:bootJar -x test

# Stage 2: 실행 환경 (가벼운 OpenJDK 사용)
FROM openjdk:17-jdk-slim
WORKDIR /app

# JAR 파일을 복사하고 이름을 app.jar로 변경
COPY --from=builder /app/game-service/build/libs/game-service-*.jar app.jar

# 컨테이너 내부 포트를 8082로 노출
EXPOSE 8082

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]