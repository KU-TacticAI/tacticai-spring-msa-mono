# Stage 1: 빌드 환경 (Gradle 사용)
FROM gradle:8.4.0-jdk17 AS builder

WORKDIR /app

# 모노레포의 모든 소스코드 복사 (Core, Game, Gateway, Common 포함)
COPY . .

RUN chmod +x ./gradlew

# Core Service만 빌드합니다. (Gradle이 Common 모듈 종속성을 자동으로 해결)
RUN ./gradlew :core-service:bootJar -x test

# Stage 2: 실행 환경 (가벼운 OpenJDK 사용)
FROM openjdk:17-jdk-slim-bullseye
WORKDIR /app

# JAR 파일을 복사하고 이름을 app.jar로 변경
COPY --from=builder /app/core-service/build/libs/core-service-*.jar app.jar

# 컨테이너 내부 포트를 8081로 노출 (Spring Boot 설정 파일에도 반영 필요)
EXPOSE 8081

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]