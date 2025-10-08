# Stage 1: 빌드 환경 (Gradle 사용)
FROM gradle:8.4.0-jdk17 AS builder

WORKDIR /app

# 모노레포의 모든 소스코드 복사
WORKDIR /app
#COPY gate-way-service ./gate-way-service
#COPY build.gradle settings.gradle gradlew gradlew.bat ./
#COPY gradle ./gradle
COPY . .

RUN chmod +x ./gradlew


# Gateway Service만 빌드합니다.
RUN ./gradlew :gate-way-service:bootJar -x test

# Stage 2: 실행 환경 (가벼운 OpenJDK 사용)
FROM openjdk:17-jdk-slim
WORKDIR /app

# JAR 파일을 복사하고 이름을 app.jar로 변경
COPY --from=builder /app/gate-way-service/build/libs/gate-way-service-*.jar app.jar

# 컨테이너 내부 포트를 8080으로 노출 (클라이언트 요청을 받음)
EXPOSE 8080

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]