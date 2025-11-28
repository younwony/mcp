# 🌤️ Spring AI MCP Server - Weather Service

MCP(Model Context Protocol) 프로토콜을 구현한 날씨 정보 제공 서버입니다.
Claude Desktop과 stdio 통신을 통해 연동되며, 한국 기상청 공식 API를 활용하여 실시간 날씨 정보를 제공합니다.

## 🎯 진짜 MCP 서버란?

### ✅ 이 프로젝트 (진짜 MCP)

```
Claude (AI) ──stdio/JSON-RPC 2.0──► MCP 서버 ──► 실제 로직 (날씨 API, 계산 등)
   ↑                                    │
   └─── AI가 도구 선택/호출 ─────────────┘
```

- **통신 방식**: stdio (표준 입출력)
- **프로토콜**: JSON-RPC 2.0
- **AI 위치**: 클라이언트 (Claude Desktop)
- **서버 역할**: 도구만 제공 (AI 없음)

### ❌ 일반 AI 챗봇 백엔드 (가짜 MCP)

```
사용자 ──HTTP──► Spring 서버 ──► OpenAI API
                    │
                    └── 서버가 AI 호출
```

- **통신 방식**: REST API (HTTP)
- **AI 위치**: 서버 측 (OpenAI 호출)
- **MCP 프로토콜**: 사용하지 않음

## 🚀 빠른 시작

### 1. 요구사항

- Java 17 이상
- Gradle 8.5 이상
- Claude Desktop (MCP 클라이언트)
- 기상청 API 키 ([공공데이터포털](https://www.data.go.kr/data/15084084/openapi.do)에서 발급)

### 2. API 키 설정

#### 2.1. API 키 발급

1. [공공데이터포털](https://www.data.go.kr/) 접속
2. 회원가입 및 로그인
3. "기상청_단기예보 조회서비스" 검색
4. 활용신청 클릭
5. 발급받은 인증키 복사

#### 2.2. API 키 등록

`src/main/resources/application.yml` 파일을 열고 `service-key` 값을 변경:

```yaml
weather:
  api:
    service-key: YOUR_API_KEY_HERE
```

또는 환경 변수로 설정:

```bash
# Windows
set WEATHER_API_SERVICE_KEY=YOUR_API_KEY_HERE

# macOS/Linux
export WEATHER_API_SERVICE_KEY=YOUR_API_KEY_HERE
```

### 3. 프로젝트 빌드

```bash
# Windows
.\gradlew clean build

# macOS/Linux
./gradlew clean build
```

### 4. Claude Desktop 설정

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json` 파일에 추가:

```json
{
  "mcpServers": {
    "weather": {
      "command": "java",
      "args": [
        "-jar",
        "-Dfile.encoding=UTF-8",
        "C:\\workspace\\intellij\\mcp\\build\\libs\\mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**macOS/Linux**: `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "weather": {
      "command": "java",
      "args": [
        "-jar",
        "-Dfile.encoding=UTF-8",
        "/absolute/path/to/mcp/build/libs/mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### 5. Claude Desktop 재시작

설정을 적용하려면 Claude Desktop을 완전히 종료하고 다시 시작합니다.

### 6. 사용하기

Claude Desktop에서 다음과 같이 질문해보세요:

```
서울의 날씨를 알려줘
```

```
부산 날씨가 궁금해
```

```
위도 37.5665, 경도 126.9780의 초단기 예보를 알려줘
```

Claude가 자동으로 MCP 서버의 도구를 호출하여 실시간 날씨 정보를 제공합니다!

## 📦 프로젝트 구조

```
mcp/
├── src/
│   ├── main/
│   │   ├── java/dev/wony/mcp/
│   │   │   ├── McpApplication.java           # Spring Boot 메인 클래스
│   │   │   └── tool/
│   │   │       ├── WeatherService.java       # 날씨 조회 서비스
│   │   │       └── dto/
│   │   │           ├── GridCoordinate.java   # 격자 좌표 변환
│   │   │           ├── WeatherApiResponse.java # API 응답 DTO
│   │   │           ├── WeatherCategory.java  # 날씨 카테고리 enum
│   │   │           ├── WeatherInfo.java      # 날씨 정보 DTO
│   │   │           └── KmaApiResponse.java   # 기상청 API 응답
│   │   └── resources/
│   │       └── application.yml               # 서버 설정 및 API 키
│   └── test/
│       └── java/dev/wony/mcp/tool/
│           ├── WeatherServiceTest.java                  # 단위 테스트
│           ├── WeatherServiceIntegrationTest.java       # 통합 테스트
│           └── dto/
│               ├── GridCoordinateTest.java              # 격자 변환 테스트
│               └── WeatherCategoryTest.java             # 카테고리 테스트
├── docs/
│   ├── KMA_API_GUIDE.md                      # 기상청 API 가이드
│   └── WEATHER_API.md                        # 날씨 API 상세 문서
├── build.gradle                              # Gradle 빌드 설정
├── CLAUDE_SETUP.md                           # Claude Desktop 연동 가이드
└── README.md                                 # 이 파일
```

## 🛠️ 제공되는 도구

### 1. 초단기실황조회 (getUltraSrtNcst)

현재 시각 기준 실황 정보를 제공합니다.

**입력**:
- `latitude` (double): 위도 (예: 37.5665)
- `longitude` (double): 경도 (예: 126.9780)

**출력**:
```
=== 초단기실황 (위도: 37.5665, 경도: 126.9780) ===
발표시각: 20241126 1400

기온: 15.2℃
1시간 강수량: 0mm
습도: 65%
강수형태: 없음
풍향: N
풍속: 2.3m/s
```

**발표시각**: 매시각 정시 (00:00, 01:00, ..., 23:00) + 10분 후 제공

### 2. 초단기예보조회 (getUltraSrtFcst)

6시간 이내 예보 정보를 제공합니다.

**입력**:
- `latitude` (double): 위도
- `longitude` (double): 경도

**출력**: 시간대별 예보 (기온, 강수, 하늘상태, 풍향, 풍속 등)

**발표시각**: 매시각 30분 (00:30, 01:30, ..., 23:30) + 15분 후 제공

### 3. 단기예보조회 (getVilageFcst)

3일 예보 정보를 제공합니다.

**입력**:
- `latitude` (double): 위도
- `longitude` (double): 경도

**출력**: 3일간 시간대별 상세 예보 (기온, 강수확률, 강수량, 적설, 풍향, 풍속, 습도 등)

**발표시각**: 하루 8회 (02, 05, 08, 11, 14, 17, 20, 23시) + 10분 후 제공

### 4. 주요 도시 날씨 조회 (getCurrentWeather)

도시 이름으로 간편하게 현재 날씨를 조회합니다.

**입력**:
- `city` (String): 도시명 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 세종, 제주 중 하나)

**출력**:
```
📍 서울 현재 날씨 (기준시각: 11월 26일 14시)

🌡️ 기온: 15.2°C
🌧️ 1시간 강수량: 없음
💧 습도: 65%
💨 풍속: 2.3m/s
☔ 강수형태: 없음
```

### 5. 지원 도시 목록 조회 (getSupportedCities)

날씨 조회가 가능한 도시 목록을 반환합니다.

**출력**:
```
날씨 조회 가능한 도시:
서울, 부산, 대구, 인천, 광주, 대전, 울산, 세종, 제주
```

**주요 도시 좌표**:
- 서울: (37.5665, 126.9780)
- 부산: (35.1796, 129.0756)
- 제주: (33.4996, 126.5312)
- 대전: (36.3504, 127.3845)
- 대구: (35.8714, 128.6014)

> **참고**: 위경도는 자동으로 기상청 격자 좌표(nx, ny)로 변환됩니다. Lambert Conformal Conic Projection 알고리즘을 사용합니다.

## 🔧 개발 가이드

### 새로운 도구 추가하기

1. `src/main/java/dev/wony/mcp/tool/` 디렉토리에 새 서비스 클래스 생성
2. `@Service` 어노테이션 사용
3. 메서드에 `@Tool` 어노테이션 추가
4. `McpApplication.java`에 Bean 등록

예시:

```java
package dev.wony.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    @Tool(description = "Translate text to another language")
    public String translate(
        @ToolParam(description = "Text to translate") String text,
        @ToolParam(description = "Target language (e.g., 'Korean', 'English')") String targetLanguage
    ) {
        // 번역 로직 구현
        return "Translated: " + text;
    }
}
```

그리고 `McpApplication.java`에 Bean 등록:

```java
@Bean
public ToolCallbackProvider translationTools(TranslationService translationService) {
    return MethodToolCallbackProvider.builder().toolObjects(translationService).build();
}
```

### 외부 API 연동 예제

```java
@Service
public class NewsService {

    @Value("${news.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public NewsService(@Value("${news.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
            .baseUrl("https://newsapi.org/v2")
            .build();
    }

    @Tool(description = "Get latest news headlines")
    public String getNews(
        @ToolParam(description = "Country code (e.g., 'kr', 'us')") String country
    ) {
        try {
            NewsResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/top-headlines")
                    .queryParam("country", country)
                    .queryParam("apiKey", apiKey)
                    .build())
                .retrieve()
                .body(NewsResponse.class);

            return formatNewsResponse(response);
        } catch (RestClientException e) {
            return "뉴스 조회 실패: " + e.getMessage();
        }
    }
}
```

## 🧪 테스트

### 단위 테스트 실행

```bash
# Windows
.\gradlew test

# macOS/Linux
./gradlew test
```

### 통합 테스트 실행

통합 테스트는 실제 기상청 API를 호출하므로 API 키가 필요합니다.

```bash
# Windows
.\gradlew test --tests "*IntegrationTest"

# macOS/Linux
./gradlew test --tests "*IntegrationTest"
```

### 테스트 커버리지 리포트

```bash
.\gradlew test jacocoTestReport
```

리포트: `build/reports/jacoco/test/html/index.html`

## 📚 기술 스택

- **Java 17**: 최신 LTS 버전
- **Spring Boot 3.5.8**: 엔터프라이즈급 프레임워크
- **Spring AI 1.1.0**: MCP 프로토콜 구현
- **Gradle 8.5**: 빌드 도구
- **RestClient**: HTTP 클라이언트
- **JUnit 5 & AssertJ**: 테스트 프레임워크

## ❓ 문제 해결

### 서버가 연결되지 않음

1. **경로 확인**: `claude_desktop_config.json`의 JAR 경로가 정확한지 확인
2. **빌드 확인**: `.\gradlew clean build`로 재빌드
3. **로그 확인**:
   - Windows: `%APPDATA%\Claude\logs\`
   - macOS: `~/Library/Logs/Claude/`

### 도구가 작동하지 않음

1. **어노테이션 확인**: `@Service`와 `@Tool` 모두 있는지 확인
2. **패키지 스캔**: `dev.wony.mcp.tool` 패키지 안에 있는지 확인
3. **Bean 등록**: `McpApplication.java`에서 `ToolCallbackProvider` Bean이 등록되었는지 확인
4. **재빌드**: 코드 변경 후 반드시 재빌드

### API 응답 오류

1. **API 키 확인**: `application.yml`에 올바른 API 키가 설정되었는지 확인
2. **호출 제한**: 기상청 API는 트래픽 제한이 있으므로 과도한 호출 주의
3. **발표 시각**: 각 API의 발표 시각에 맞춰 데이터가 업데이트되므로 시간 확인

### Java 버전 오류

```bash
java -version  # Java 17 이상 확인
```

Java 17이 없다면 [Adoptium](https://adoptium.net/)에서 다운로드하세요.

## 🔍 MCP 프로토콜 작동 원리

### 1. 초기화 (Initialization)

Claude Desktop이 MCP 서버를 시작하면:

```json
→ Claude: {"jsonrpc":"2.0","method":"initialize","params":{...},"id":1}
← Server: {"jsonrpc":"2.0","result":{"protocolVersion":"1.0.0"},"id":1}
```

### 2. 도구 목록 조회

```json
→ Claude: {"jsonrpc":"2.0","method":"tools/list","params":{},"id":2}
← Server: {
    "jsonrpc":"2.0",
    "result":{
      "tools":[
        {"name":"getUltraSrtNcst","description":"Get current weather observation..."},
        {"name":"getUltraSrtFcst","description":"Get ultra short-term forecast..."},
        {"name":"getVilageFcst","description":"Get short-term forecast..."},
        {"name":"getCurrentWeather","description":"한국 주요 도시의 현재 날씨를 조회..."},
        {"name":"getSupportedCities","description":"날씨 조회가 가능한 한국 주요 도시 목록..."}
      ]
    },
    "id":2
  }
```

### 3. 도구 호출

```json
→ Claude: {
    "jsonrpc":"2.0",
    "method":"tools/call",
    "params":{
      "name":"getCurrentWeather",
      "arguments":{"city":"서울"}
    },
    "id":3
  }
← Server: {
    "jsonrpc":"2.0",
    "result":"📍 서울 현재 날씨 (기준시각: 11월 26일 14시)\n\n🌡️ 기온: 15.2°C\n...",
    "id":3
  }
```

## 🔗 참고 자료

- [MCP 공식 문서](https://modelcontextprotocol.io/)
- [MCP 명세서](https://spec.modelcontextprotocol.io/)
- [Spring AI MCP Server](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [Claude Desktop 다운로드](https://claude.ai/download)
- [Anthropic 블로그 - MCP 소개](https://www.anthropic.com/news/model-context-protocol)
- [기상청 API 문서](https://www.data.go.kr/data/15084084/openapi.do)
- [상세 날씨 API 가이드](docs/WEATHER_API.md)
- [기상청 API 활용 가이드](docs/KMA_API_GUIDE.md)

## 📝 라이선스

MIT License

## 🤝 기여

버그 리포트나 기능 제안은 이슈로 등록해주세요!

---

**Made with ❤️ by [dev.wony](https://github.com/wony)**

이 서버는 진정한 MCP 프로토콜을 구현합니다. REST API나 HTTP 통신을 사용하지 않으며, Claude Desktop과 stdio를 통해 직접 통신합니다.
