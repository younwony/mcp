# Spring AI MCP Server

진정한 MCP(Model Context Protocol) 프로토콜을 구현한 서버입니다. Claude Desktop과 stdio 통신을 통해 연동됩니다.

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

### 2. 프로젝트 빌드

```bash
cd C:\workspace\intellij\mcp
.\gradlew clean build
```

### 3. Claude Desktop 설정

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json` 파일에 추가:

```json
{
  "mcpServers": {
    "weather-calculator": {
      "command": "java",
      "args": [
        "-jar",
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
    "weather-calculator": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/mcp/build/libs/mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### 4. Claude Desktop 재시작

설정을 적용하려면 Claude Desktop을 완전히 종료하고 다시 시작합니다.

### 5. 사용하기

Claude Desktop에서 다음과 같이 질문해보세요:

```
서울의 날씨를 알려줘
```

```
15 * 23 + 47을 계산해줘
```

Claude가 자동으로 MCP 서버의 도구를 호출합니다!

## 📦 프로젝트 구조

```
mcp/
├── src/main/java/com/example/mcp/
│   ├── McpApplication.java           # Spring Boot 메인 클래스
│   └── tool/
│       ├── WeatherTool.java          # 날씨 조회 도구
│       └── CalculatorTool.java       # 계산기 도구
├── src/main/resources/
│   └── application.yml                # MCP 서버 설정
├── build.gradle                       # Gradle 빌드 설정
├── claude_desktop_config.json         # Claude Desktop 설정 예제
├── CLAUDE_SETUP.md                    # 상세 연동 가이드
└── README.md                          # 이 파일
```

## 🛠️ 제공되는 도구

### 1. get_weather

특정 지역의 날씨 정보를 제공합니다.

**입력**:
- `location` (String): 도시 이름 (예: "Seoul", "Tokyo")

**출력**:
```json
{
  "location": "Seoul",
  "temperature": "15°C",
  "condition": "Partly Cloudy",
  "humidity": "65%",
  "description": "Seoul is currently experiencing partly cloudy conditions..."
}
```

**참고**: 현재는 모의 데이터를 반환합니다. 실제 프로덕션에서는 OpenWeatherMap 등의 API를 연동하세요.

### 2. calculate

수학 표현식을 계산합니다.

**입력**:
- `expression` (String): 수학 표현식 (예: "2 + 2", "15 * 23 + 47")

**출력**:
```json
{
  "expression": "15 * 23 + 47",
  "result": "392"
}
```

**지원 연산**: `+`, `-`, `*`, `/`, `()` (괄호)

## 🔧 개발 가이드

### 새로운 도구 추가하기

1. `src/main/java/com/example/mcp/tool/` 디렉토리에 새 클래스 생성
2. `@Component`와 `@McpTool` 어노테이션 사용

예시:

```java
package com.example.mcp.tool;

import org.springframework.ai.mcp.server.McpTool;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TranslationTool {

    @McpTool(
        name = "translate",
        description = "Translate text to another language"
    )
    public Map<String, Object> translate(
        @McpTool.Param(description = "Text to translate") String text,
        @McpTool.Param(description = "Target language (e.g., 'Korean', 'English')") String targetLanguage
    ) {
        // 실제 번역 API 호출 (예: Google Translate, DeepL)
        String translated = callTranslationAPI(text, targetLanguage);

        return Map.of(
            "original", text,
            "translated", translated,
            "targetLanguage", targetLanguage
        );
    }

    private String callTranslationAPI(String text, String targetLang) {
        // 번역 로직 구현
        return "Translated: " + text;
    }
}
```

3. 프로젝트 재빌드:

```bash
.\gradlew clean build
```

4. Claude Desktop 재시작

### 외부 API 연동 예제

실제 날씨 API를 연동하려면:

```java
@Component
public class WeatherTool {

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @McpTool(name = "get_weather", description = "Get real weather data")
    public Map<String, Object> getWeather(
        @McpTool.Param(description = "City name") String location
    ) {
        String url = String.format(
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
            location, apiKey
        );

        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

        return Map.of(
            "location", location,
            "temperature", response.getMain().getTemp() + "°C",
            "condition", response.getWeather().get(0).getDescription()
        );
    }
}
```

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
        {"name":"get_weather","description":"Get the current weather..."},
        {"name":"calculate","description":"Perform mathematical..."}
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
      "name":"calculate",
      "arguments":{"expression":"15 * 23 + 47"}
    },
    "id":3
  }
← Server: {
    "jsonrpc":"2.0",
    "result":{
      "expression":"15 * 23 + 47",
      "result":"392"
    },
    "id":3
  }
```

## 🧪 테스트

### 단위 테스트 실행

```bash
.\gradlew test
```

### 통합 테스트

```bash
.\gradlew integrationTest
```

## 📚 기술 스택

- **Java 17**: 최신 LTS 버전
- **Spring Boot 3.3.5**: 엔터프라이즈급 프레임워크
- **Spring AI MCP Server 1.0.0-M6**: MCP 프로토콜 구현
- **Gradle 8.5**: 빌드 도구
- **Lombok**: 코드 간소화
- **Jackson**: JSON 처리

## ❓ 문제 해결

### 서버가 연결되지 않음

1. **경로 확인**: `claude_desktop_config.json`의 JAR 경로가 정확한지 확인
2. **빌드 확인**: `.\gradlew clean build`로 재빌드
3. **로그 확인**:
   - Windows: `%APPDATA%\Claude\logs\`
   - macOS: `~/Library/Logs/Claude/`

### 도구가 작동하지 않음

1. **어노테이션 확인**: `@Component`와 `@McpTool` 모두 있는지 확인
2. **패키지 스캔**: `com.example.mcp.tool` 패키지 안에 있는지 확인
3. **재빌드**: 코드 변경 후 반드시 재빌드

### Java 버전 오류

```bash
java -version  # Java 17 이상 확인
```

Java 17이 없다면 [Adoptium](https://adoptium.net/)에서 다운로드하세요.

## 🔗 참고 자료

- [MCP 공식 문서](https://modelcontextprotocol.io/)
- [MCP 명세서](https://spec.modelcontextprotocol.io/)
- [Spring AI MCP Server](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [Claude Desktop 다운로드](https://claude.ai/download)
- [Anthropic 블로그 - MCP 소개](https://www.anthropic.com/news/model-context-protocol)

## 📝 라이선스

MIT License

## 🤝 기여

버그 리포트나 기능 제안은 이슈로 등록해주세요!

---

**중요 알림**: 이 서버는 진정한 MCP 프로토콜을 구현합니다. REST API나 HTTP 통신을 사용하지 않으며, Claude Desktop과 stdio를 통해 직접 통신합니다.
