# MCP (Model Context Protocol) 가이드

## MCP란?

**MCP (Model Context Protocol)**는 AI 애플리케이션과 외부 데이터 소스를 연결하는 개방형 프로토콜입니다. Claude와 같은 AI 모델이 다양한 데이터와 도구에 안전하게 접근할 수 있도록 표준화된 방법을 제공합니다.

### 핵심 개념

MCP는 크게 **3가지 주요 구성 요소**로 이루어져 있습니다:

1. **Tools (도구)**
2. **Resources (리소스)**
3. **Prompts (프롬프트)**

---

## 1. Tools (도구)

### 개념

**Tools**는 AI가 **실행할 수 있는 함수**입니다. AI가 특정 작업을 수행하기 위해 호출할 수 있는 기능을 제공합니다.

### 특징

- **능동적 (Active)**: AI가 필요할 때 능동적으로 호출
- **실행 가능 (Executable)**: 함수를 실행하여 결과를 반환
- **파라미터 입력**: 사용자 또는 AI가 파라미터를 제공

### 예시

이 프로젝트의 Weather MCP 서버는 다음과 같은 **Tools**를 제공합니다:

#### 1) `getUltraSrtNcst` - 초단기실황 조회
```java
@Tool(description = "Get current weather observation for a specific latitude/longitude in Korea")
public String getUltraSrtNcst(
    @ToolParam(description = "Latitude (위도)") double latitude,
    @ToolParam(description = "Longitude (경도)") double longitude
)
```
- **입력**: 위도, 경도
- **출력**: 현재 날씨 실황 (기온, 강수량, 풍속 등)

#### 2) `getUltraSrtFcst` - 초단기예보 조회 (6시간)
```java
@Tool(description = "Get ultra short-term weather forecast (up to 6 hours)")
public String getUltraSrtFcst(double latitude, double longitude)
```
- **입력**: 위도, 경도
- **출력**: 6시간 이내 시간별 날씨 예보

#### 3) `getVilageFcst` - 단기예보 조회 (3일)
```java
@Tool(description = "Get short-term weather forecast (up to 3 days)")
public String getVilageFcst(double latitude, double longitude)
```
- **입력**: 위도, 경도
- **출력**: 3일간의 상세 날씨 예보

#### 4) `getCurrentWeather` - 도시명으로 현재 날씨 조회
```java
@Tool(description = "한국 주요 도시의 현재 날씨를 조회합니다")
public String getCurrentWeather(String city)
```
- **입력**: 도시명 (서울, 부산, 대구 등)
- **출력**: 해당 도시의 현재 날씨

#### 5) `getSupportedCities` - 지원 도시 목록 조회
```java
@Tool(description = "날씨 조회가 가능한 한국 주요 도시 목록을 반환합니다")
public String getSupportedCities()
```
- **입력**: 없음
- **출력**: 지원하는 도시 목록

### Tool 사용 예시

**Claude에게 질문:**
> "서울의 현재 날씨를 알려줘"

**Claude의 동작:**
1. `getCurrentWeather` Tool 호출
2. 파라미터: `city = "서울"`
3. MCP 서버가 기상청 API 호출
4. 결과를 사람이 읽기 쉬운 형태로 반환

---

## 2. Resources (리소스)

### 개념

**Resources**는 AI가 **읽을 수 있는 데이터 소스**입니다. AI에게 컨텍스트를 제공하기 위한 정적 또는 동적 데이터를 의미합니다.

### 특징

- **수동적 (Passive)**: AI가 필요시 데이터를 읽어옴
- **읽기 전용 (Read-only)**: 데이터를 조회만 하고 변경하지 않음
- **컨텍스트 제공**: AI에게 배경 지식이나 참고 자료를 제공

### 예시

Resources의 일반적인 사용 예:

- **파일 시스템**: 프로젝트의 README, 문서 파일
- **데이터베이스**: 고객 정보, 제품 카탈로그
- **API 응답**: 최신 뉴스, 날씨 정보
- **웹 페이지**: 사이트 콘텐츠, 위키 문서

### 현재 프로젝트 상태

**이 프로젝트는 현재 Resources를 제공하지 않습니다.**

필요한 경우 다음과 같은 Resources를 추가할 수 있습니다:
- 기상청 API 문서
- 지역별 격자 좌표 매핑 테이블
- 날씨 코드 설명 문서

---

## 3. Prompts (프롬프트)

### 개념

**Prompts**는 AI에게 제공하는 **사전 정의된 명령어 템플릿**입니다. 자주 사용되는 작업을 간편하게 실행할 수 있도록 돕습니다.

### 특징

- **템플릿 기반**: 미리 정의된 프롬프트 형식
- **재사용 가능**: 반복적인 작업을 효율화
- **파라미터 지원**: 동적으로 값을 삽입 가능

### 예시

Prompts의 사용 예:

```
# 날씨 비교 프롬프트
"서울과 부산의 현재 기온을 비교해줘"

# 예보 요약 프롬프트
"내일 {도시}의 날씨를 요약해줘"
```

### 현재 프로젝트 상태

**이 프로젝트는 현재 Prompts를 제공하지 않습니다.**

---

## MCP 서버 구조

### 프로젝트 아키텍처

```
mcp/
├── src/main/java/dev/wony/mcp/
│   ├── McpApplication.java              # Spring Boot 애플리케이션
│   └── tool/weather/
│       ├── WeatherService.java          # MCP Tool 구현 (@Tool)
│       ├── dto/                         # 데이터 전송 객체
│       │   ├── Coordinate.java          # 위경도 좌표
│       │   ├── GridCoordinate.java      # 격자 좌표
│       │   ├── WeatherApiResponse.java  # API 응답 DTO
│       │   ├── WeatherCategory.java     # 날씨 카테고리 enum
│       │   ├── SkyCondition.java        # 하늘 상태 enum
│       │   ├── PrecipitationType.java   # 강수 형태 enum
│       │   └── WindDirection.java       # 풍향 값 객체
│       └── util/                        # 유틸리티 클래스
│           ├── CoordinateConverter.java # 좌표 변환
│           └── WeatherCodeInterpreter.java # 날씨 코드 해석
```

### MCP Tool 등록 방법

Spring AI의 `@Tool` 어노테이션을 사용하여 메서드를 MCP Tool로 등록합니다:

```java
@Service
public class WeatherService {

    @Tool(description = "도구에 대한 설명")
    public String toolMethod(
        @ToolParam(description = "파라미터 설명") String param
    ) {
        // 구현 내용
        return "결과";
    }
}
```

### MCP 도구 제공

`McpApplication.java`에서 `ToolCallbackProvider` Bean을 등록하여 MCP 도구를 제공합니다:

```java
@Bean
public ToolCallbackProvider weatherTools(WeatherService weatherService) {
    return ToolCallbackProvider.of(weatherService);
}
```

---

## Tools vs Resources vs Prompts 비교

| 구분 | Tools | Resources | Prompts |
|------|-------|-----------|---------|
| **역할** | 실행 가능한 함수 | 읽기 가능한 데이터 | 명령어 템플릿 |
| **특징** | 능동적 | 수동적 | 재사용 가능 |
| **입력** | 파라미터 | 데이터 경로/ID | 템플릿 변수 |
| **출력** | 실행 결과 | 데이터 내용 | AI 응답 |
| **예시** | API 호출, 계산 | 문서, 데이터베이스 | 작업 템플릿 |

---

## 이 프로젝트의 MCP 구성 요소

### ✅ 구현된 기능

- **Tools**: 5개의 날씨 조회 도구
  - `getUltraSrtNcst` - 초단기실황
  - `getUltraSrtFcst` - 초단기예보
  - `getVilageFcst` - 단기예보
  - `getCurrentWeather` - 도시별 날씨
  - `getSupportedCities` - 도시 목록

### ❌ 미구현 기능

- **Resources**: 없음
- **Prompts**: 없음

---

## 사용 방법

### 1. MCP 서버 실행

```bash
./gradlew bootRun
```

### 2. Claude에서 사용

Claude Desktop 또는 Claude API를 통해 MCP 서버에 연결하면, Claude가 날씨 정보를 조회할 수 있습니다.

**예시 대화:**

```
사용자: "서울의 현재 날씨를 알려줘"

Claude: [getCurrentWeather Tool 호출]
📍 서울 현재 날씨 (기준시각: 11월 29일 14시)

🌡️ 기온: 12.3°C
🌧️ 1시간 강수량: 없음
💧 습도: 45%
💨 풍속: 2.1m/s
☔ 강수형태: 없음
```

---

## 기상청 API 연동

이 MCP 서버는 **기상청 단기예보 조회 서비스**를 사용합니다:

- **API 문서**: https://www.data.go.kr/data/15084084/openapi.do
- **좌표 변환**: Lambert Conformal Conic Projection
- **지원 지역**: 대한민국 전역

### 주요 API

1. **초단기실황조회** (`getUltraSrtNcst`)
   - 발표: 매시각 정시
   - 업데이트: 10분마다

2. **초단기예보조회** (`getUltraSrtFcst`)
   - 발표: 매시각 30분
   - 예보 기간: 6시간

3. **단기예보조회** (`getVilageFcst`)
   - 발표: 하루 8회 (02, 05, 08, 11, 14, 17, 20, 23시)
   - 예보 기간: 3일

---

## 참고 자료

- [MCP 공식 문서](https://modelcontextprotocol.io/)
- [Spring AI 문서](https://docs.spring.io/spring-ai/reference/)
- [기상청 API 가이드](https://www.data.go.kr/data/15084084/openapi.do)

---

## 확장 가능성

### 추가 가능한 Tools

- `getWeatherAlert` - 기상 특보 조회
- `getForecastSummary` - 주간 예보 요약
- `getAirQuality` - 대기질 정보 조회

### 추가 가능한 Resources

- 기상 용어 사전
- 지역별 기후 특성 데이터
- 과거 날씨 데이터

### 추가 가능한 Prompts

- 날씨 기반 옷차림 추천
- 외출 최적 시간대 분석
- 지역별 날씨 비교 리포트

---

## 라이선스

이 프로젝트는 학습 및 연구 목적으로 작성되었습니다.
