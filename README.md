
# 🧠 TacticAI - Spring MSA 기반 AI 게임 플랫폼

> AI vs AI 보드 게임 매칭 플랫폼  
> Kotlin & Spring Boot 기반 MSA 아키텍처 + Python(FastAPI) 게임 서버 연동  
> 실시간 매칭, JWT 인증, Redis Pub/Sub, 비동기 메시지 처리 등 도입

---

## 📁 프로젝트 구조

```bash
tacticai-spring-msa-mono/
├── common-module/        # 공통 유틸, JWT, 토큰 필터 등
├── gate-way-service/     # WebFlux 기반 API Gateway
├── core-service/         # 유저 관리, 매칭 로직 등 핵심 도메인 서비스
├── game-service/         # 게임 실행 로직 및 FastAPI 연동 서비스
├── build.gradle.kts      # 전체 프로젝트 빌드 스크립트 (Kotlin DSL)
└── settings.gradle.kts   # 멀티모듈 설정
```

---

## 🚀 주요 기술 스택

| 영역 | 기술 스택 |
|------|----------|
| 백엔드 프레임워크 | Spring Boot 3.x (Kotlin) |
| 게임 서버 | Python FastAPI (비동기, AI 연동) |
| API Gateway | Spring WebFlux + JWT 필터 |
| 인증/보안 | JWT (Access + Refresh), Redis 기반 토큰 관리 |
| 메시징 | Redis Pub/Sub (게임 메시지 브로커) |
| 빌드 도구 | Gradle (Kotlin DSL) |
| 배포 방식 | Docker + EC2 or Lambda (예정) |
| 문서화 | Swagger |

---

## 🔌 모듈별 역할

### 🧩 common-module
- JWTUtil, TokenSettings, 공통 응답 객체 등 재사용 모듈
- Spring Security 또는 WebFlux 공통 코드 미포함 → Gateway 전용 유틸은 따로 관리

### 🌐 gate-way-service
- WebFlux 기반 API Gateway
- `/api/**` 요청 라우팅 및 인증 필터 적용
- JWT 토큰 유효성 검증 및 Redis 연동

### 🧍 core-service
- 유저 정보, 게임 매칭, 랭킹 관리 등 핵심 비즈니스 로직
- Spring WebMVC + REST API 구성
- RabbitMQ 또는 Redis PubSub로 게임 요청 전달

### 🎮 game-service
- 실제 게임 로직 (FastAPI 서버)와의 통신 담당
- 게임 세션 생성, 게임 로그 저장 등
- 내부적으로 Python 서버와 HTTP 통신 수행

---

## 📌 참고사항

- API Gateway에서 WebFlux 기반이므로 `common-module`의 Servlet 기반 필터를 그대로 사용할 수 없습니다. → 별도 구현 필요
- Redis, Docker, Spring Config 분리 등을 통해 확장성 고려

---

## 👨‍💻 팀원 및 기여

| 이름  | 역할 |
|-----|------|
| 이경민 | FastAPI 기반 AI 게임 로직 개발 |
| 이서준 | 백엔드 개발 (Core, Gateway, 인프라 구조, 보안 로직) |
| 조서현 | React 프론트엔드 개발 및 디자인 |
