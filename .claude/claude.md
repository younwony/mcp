# 프로젝트 개발 가이드라인

## 테스트 코드 작성 규칙

### 필수 사항
- **모든 새로운 코드에는 단위 테스트를 필수로 작성합니다.**
- 테스트 커버리지는 최소 80% 이상을 목표로 합니다.
- TDD(Test-Driven Development) 방식을 권장합니다.

### 테스트 작성 기준

#### 1. 단위 테스트 (Unit Test)
- 각 클래스와 메서드는 독립적인 단위 테스트를 가져야 합니다.
- Mock 객체를 사용하여 외부 의존성을 격리합니다.
- JUnit 5와 Mockito를 사용합니다.

**테스트해야 할 항목:**
- 유틸리티 클래스의 모든 공개 메서드
- 서비스 레이어의 비즈니스 로직
- DTO 변환 및 데이터 매핑
- 예외 처리 케이스
- Edge cases 및 경계값 테스트

#### 2. 통합 테스트 (Integration Test)
- API 엔드포인트 테스트
- 데이터베이스 연동 테스트
- 외부 API 호출 테스트 (Mock 서버 사용)

#### 3. 테스트 명명 규칙
```java
// Given-When-Then 패턴 사용
@Test
void methodName_StateUnderTest_ExpectedBehavior() {
    // given
    // when
    // then
}
```

#### 4. 테스트 구조
```java
@DisplayName("클래스 설명")
class ClassNameTest {

    @Nested
    @DisplayName("메서드명")
    class MethodName {

        @Test
        @DisplayName("정상 케이스 설명")
        void shouldReturnExpectedValue_whenValidInput() {
            // test implementation
        }

        @Test
        @DisplayName("예외 케이스 설명")
        void shouldThrowException_whenInvalidInput() {
            // test implementation
        }
    }
}
```

### 테스트 도구

#### 필수 의존성
```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.mockito:mockito-core'
testImplementation 'org.mockito:mockito-junit-jupiter'
```

#### AssertJ 사용
- 더 읽기 쉬운 assertion을 위해 AssertJ를 사용합니다.
```java
assertThat(actual).isEqualTo(expected);
assertThat(list).hasSize(3).contains("item1", "item2");
```

### MCP 도구 테스트

#### @Tool 메서드 테스트
- MCP 도구로 등록된 메서드는 반드시 단위 테스트를 작성합니다.
- 다양한 입력 시나리오를 테스트합니다:
  - 정상 입력
  - 잘못된 입력
  - 경계값
  - null/empty 처리

#### 외부 API 호출 테스트
- RestClient를 Mock 처리합니다.
- WireMock 또는 MockWebServer를 사용하여 HTTP 응답을 시뮬레이션합니다.

### 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "ClassName"

# 테스트 커버리지 리포트 생성
./gradlew test jacocoTestReport
```

### 커밋 전 체크리스트

- [ ] 모든 새로운 코드에 단위 테스트 작성
- [ ] 모든 테스트 통과 확인
- [ ] 테스트 커버리지 80% 이상 확인
- [ ] Edge cases 테스트 작성
- [ ] 예외 처리 테스트 작성

## 코드 스타일

### Lombok 사용
- `@Data`, `@Builder`, `@Slf4j` 등 적극 활용
- 보일러플레이트 코드 최소화

### 로깅
- SLF4J를 사용한 로깅
- 적절한 로그 레벨 사용 (DEBUG, INFO, WARN, ERROR)
- 민감한 정보는 로그에 포함하지 않음

### 예외 처리
- 커스텀 예외 클래스 사용
- 의미 있는 에러 메시지 제공
- 예외를 무시하지 않음

## MCP 서버 개발

### 도구 메서드 작성
- `@Tool` 어노테이션 사용
- 명확한 description 작성
- `@ToolParam`으로 파라미터 설명 제공
- 사람이 읽기 쉬운 응답 형식 반환

### API 통합
- RestClient 사용
- 적절한 타임아웃 설정
- 재시도 로직 구현 (필요시)
- 에러 응답 처리
