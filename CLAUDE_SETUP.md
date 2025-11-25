# Claude Desktop 연동 가이드

이 문서는 MCP 서버를 Claude Desktop과 연동하는 방법을 설명합니다.

## 1. MCP 서버 빌드

먼저 프로젝트를 빌드하여 실행 가능한 JAR 파일을 생성합니다.

```bash
cd C:\workspace\intellij\mcp
.\gradlew clean build
```

빌드가 성공하면 `build/libs/mcp-0.0.1-SNAPSHOT.jar` 파일이 생성됩니다.

## 2. Claude Desktop 설정 파일 수정

Claude Desktop의 설정 파일에 MCP 서버를 등록해야 합니다.

### Windows

설정 파일 위치: `%APPDATA%\Claude\claude_desktop_config.json`

파일이 없다면 생성하고, 다음 내용을 추가합니다:

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

### macOS/Linux

설정 파일 위치: `~/Library/Application Support/Claude/claude_desktop_config.json` (macOS)

```json
{
  "mcpServers": {
    "weather-calculator": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/your/mcp/build/libs/mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**중요**: 경로는 절대 경로를 사용해야 하며, 실제 프로젝트 경로에 맞게 수정하세요.

## 3. Claude Desktop 재시작

설정을 적용하려면 Claude Desktop을 완전히 종료하고 다시 시작합니다.

## 4. 연동 확인

Claude Desktop에서 MCP 서버가 정상적으로 연결되었는지 확인합니다:

1. Claude Desktop 우측 하단의 🔌 아이콘 확인
2. "weather-calculator" 서버가 활성화되었는지 확인
3. 사용 가능한 도구: `get_weather`, `calculate`

## 5. 사용 예제

Claude Desktop에서 다음과 같이 질문해보세요:

```
서울의 날씨를 알려줘
```

```
15 * 23 + 47을 계산해줘
```

Claude가 자동으로 MCP 서버의 도구를 호출하여 결과를 제공합니다.

## 문제 해결

### 서버가 연결되지 않는 경우

1. **JAR 파일 경로 확인**: 설정 파일의 경로가 정확한지 확인
2. **Java 설치 확인**: `java -version` 명령어로 Java 17 이상이 설치되었는지 확인
3. **로그 확인**: Claude Desktop의 로그 파일 확인
   - Windows: `%APPDATA%\Claude\logs\`
   - macOS: `~/Library/Logs/Claude/`

### 도구가 작동하지 않는 경우

1. **서버 재빌드**: 코드를 수정한 경우 `.\gradlew clean build` 다시 실행
2. **Claude Desktop 재시작**: 설정 변경 후 반드시 재시작
3. **로그 확인**: 애플리케이션 로그에서 오류 메시지 확인

## 추가 도구 개발

새로운 도구를 추가하려면:

1. `src/main/java/com/example/mcp/tool/` 디렉토리에 새 클래스 생성
2. `@Component` 어노테이션 추가
3. `@McpTool` 어노테이션으로 메서드 정의
4. 빌드 후 Claude Desktop 재시작

예시:

```java
@Component
public class TranslationTool {
    @McpTool(
        name = "translate",
        description = "Translate text to another language"
    )
    public Map<String, Object> translate(
        @McpTool.Param(description = "Text to translate") String text,
        @McpTool.Param(description = "Target language") String targetLanguage
    ) {
        // 번역 로직 구현
        return Map.of("translated", translatedText);
    }
}
```

## 참고 자료

- [MCP 공식 문서](https://modelcontextprotocol.io/)
- [Claude Desktop 다운로드](https://claude.ai/download)
- [Spring AI MCP Server](https://docs.spring.io/spring-ai/reference/api/mcp.html)
