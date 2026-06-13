# evenly-backend

더치페이 모임 정산기 백엔드.

## 스택
- Java 21, Spring Boot 3.5.3, Gradle
- PostgreSQL + Flyway (`ddl-auto: validate`)
- spotless(palantir-java-format), JUnit5 + Testcontainers + ArchUnit + jqwik
- 배포: Jenkins + Docker (hobom 공유 파이프라인)

## 로컬 실행
```bash
cp .env.example .env   # DB 접속정보 채우기
./gradlew bootRun
# Swagger: http://localhost:8080/swagger-ui.html
```

## 빌드 / 검증
```bash
./gradlew build         # 컴파일 + 테스트
./gradlew verify        # spotlessCheck + test
./gradlew spotlessApply # 포맷 적용
```

## 배포 (Jenkins + Docker)
- `Dockerfile` — multi-stage (temurin 21 build → jre-alpine), 비루트 실행, 8080 노출.
- `Jenkinsfile` — `hobom-shared-lib`의 `hobomPipeline` 사용 (hammer 와 동일 패턴).
