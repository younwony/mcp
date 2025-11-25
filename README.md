# Spring Boot AI MCP Server

Spring Boot AI를 활용한 간단한 MCP(Model Context Protocol) 서버 구현입니다.

## MCP(Model Context Protocol)란?

### 개요

MCP(Model Context Protocol)는 Anthropic이 개발한 개방형 표준 프로토콜로, AI 모델과 외부 도구(Tools) 및 데이터 소스를 안전하게 연결하기 위한 통신 규약입니다. 2024년에 공개된 이 프로토콜은 AI 애플리케이션이 다양한 컨텍스트와 기능에 접근할 수 있도록 표준화된 방법을 제공합니다.

### MCP의 주요 개념

#### 1. 아키텍처

MCP는 클라이언트-서버 아키텍처를 기반으로 합니다:

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   AI Client     │ ◄─────► │   MCP Server    │ ◄─────► │  Data Sources   │
│  (Claude, GPT)  │   MCP   │  (Your Server)  │         │  (APIs, DBs)    │
└─────────────────┘         └─────────────────┘         └─────────────────┘
```

- **MCP Client**: AI 모델이나 애플리케이션 (예: Claude, ChatGPT)
- **MCP Server**: 도구와 리소스를 제공하는 서버 (이 프로젝트)
- **Data Sources**: 실제 데이터나 기능을 제공하는 외부 시스템

#### 2. 핵심 구성 요소

##### Tools (도구)
- AI가 실행할 수 있는 함수나 기능
- 예: 날씨 조회, 계산, 데이터베이스 쿼리, API 호출
- JSON Schema로 입력/출력 형식을 정의

##### Resources (리소스)
- AI가 접근할 수 있는 데이터나 컨텍스트
- 예: 파일, 문서, 데이터베이스 레코드
- URI 기반으로 식별

##### Prompts (프롬프트)
- 재사용 가능한 프롬프트 템플릿
- 특정 작업을 위한 사전 정의된 지침

#### 3. 통신 방식

MCP는 다양한 전송(Transport) 방식을 지원합니다:

- **Standard I/O (stdio)**: 프로세스 간 직접 통신
- **HTTP/SSE**: 웹 기반 통신 (이 프로젝트에서 사용)
- **WebSocket**: 실시간 양방향 통신

메시지는 JSON-RPC 2.0 형식을 따릅니다:

```json
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "params": {
    "name": "get_weather",
    "arguments": {
      "location": "Seoul"
    }
  },
  "id": 1
}
```

### MCP의 장점

#### 1. 표준화
- AI 도구 통합을 위한 일관된 인터페이스
- 다양한 AI 모델과 애플리케이션 간 호환성
- 재사용 가능한 컴포넌트 개발 가능

#### 2. 보안
- 권한 관리 및 접근 제어
- 샌드박스 환경에서 안전한 실행
- 민감한 데이터 보호

#### 3. 확장성
- 플러그인 방식으로 기능 추가
- 독립적인 서버 개발 및 배포
- 다양한 프로그래밍 언어 지원

#### 4. 컨텍스트 공유
- AI가 필요한 정보에 직접 접근
- 실시간 데이터 통합
- 멀티모달 컨텍스트 지원

### 사용 사례

#### 1. 데이터베이스 통합
```
AI ─ MCP ─ Database Server ─ PostgreSQL
```
AI가 자연어 쿼리를 통해 데이터베이스에 접근

#### 2. API 통합
```
AI ─ MCP ─ API Server ─ External APIs (Slack, GitHub, etc.)
```
AI가 외부 서비스와 상호작용

#### 3. 파일 시스템 접근
```
AI ─ MCP ─ File Server ─ Local/Cloud Storage
```
AI가 파일을 읽고 분석

#### 4. 개발 도구
```
AI ─ MCP ─ Dev Tools Server ─ Git, Build Tools, Testing
```
AI 기반 코드 어시스턴트 구현

### MCP vs 기존 방식

#### 기존 방식 (Function Calling)
```java
// AI 모델에 함수 정의를 직접 전달
functions = [
  {name: "get_weather", params: {...}},
  {name: "calculate", params: {...}}
]
response = ai.chat(message, functions)
```

**단점**:
- AI 모델마다 다른 형식
- 함수가 AI와 강하게 결합
- 확장성 제한

#### MCP 방식
```java
// 독립적인 MCP 서버가 도구 제공
mcpServer.registerTool("get_weather", schema, handler)
ai.connectToMCP(mcpServerUrl)
```

**장점**:
- 표준화된 프로토콜
- 서버 독립적으로 개발
- 여러 AI 모델에서 재사용

### 이 프로젝트에서의 MCP 구현

이 Spring Boot 프로젝트는 간단한 MCP 서버를 구현합니다:

1. **Tools 제공**: `get_weather`, `calculate` 등의 도구를 정의
2. **HTTP API**: REST 엔드포인트를 통해 MCP 프로토콜 지원
3. **Spring AI 통합**: OpenAI API를 활용하여 AI 기능 제공
4. **확장 가능**: 새로운 도구를 쉽게 추가할 수 있는 구조

### MCP 생태계

- **공식 SDK**: Python, TypeScript, Java 등 다양한 언어 지원
- **커뮤니티 서버**: 데이터베이스, 파일 시스템, API 통합 등
- **Claude Desktop**: MCP를 네이티브로 지원하는 AI 클라이언트
- **개발 도구**: MCP Inspector, 디버깅 도구 등

### 더 알아보기

- [MCP 공식 사이트](https://modelcontextprotocol.io/)
- [MCP GitHub](https://github.com/modelcontextprotocol)
- [MCP 명세서](https://spec.modelcontextprotocol.io/)
- [Anthropic 블로그 - MCP 소개](https://www.anthropic.com/news/model-context-protocol)

## 프로젝트 개요

이 프로젝트는 Spring Boot 3.3.5와 Spring AI를 사용하여 MCP 서버를 구현한 예제입니다. OpenAI API를 활용하여 AI 기능을 제공하며, 다양한 도구(Tool)를 통해 기능을 확장할 수 있습니다.

## 기술 스택

- Java 21
- Spring Boot 3.3.5
- Spring AI 1.0.0-M3
- Gradle 8.5
- Lombok
- Jackson (JSON processing)

## 프로젝트 구조

```
mcp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/mcp/
│   │   │       ├── controller/
│   │   │       │   └── McpController.java        # REST API 컨트롤러
│   │   │       ├── model/
│   │   │       │   ├── McpRequest.java           # 요청 모델
│   │   │       │   ├── McpResponse.java          # 응답 모델
│   │   │       │   └── McpTool.java              # 도구 정의 모델
│   │   │       ├── service/
│   │   │       │   └── McpService.java           # 비즈니스 로직
│   │   │       └── McpApplication.java           # 메인 애플리케이션
│   │   └── resources/
│   │       └── application.yml                    # 설정 파일
│   └── test/
│       └── java/
├── build.gradle                                   # Gradle 빌드 설정
└── settings.gradle                                # Gradle 프로젝트 설정
```

## 환경 설정

### 1. OpenAI API 키 설정

프로젝트를 실행하기 전에 OpenAI API 키를 환경 변수로 설정해야 합니다.

#### Windows
```cmd
set OPENAI_API_KEY=your-api-key-here
```

#### Linux/Mac
```bash
export OPENAI_API_KEY=your-api-key-here
```

### 2. application.yml 설정

`src/main/resources/application.yml` 파일에서 다음 설정을 확인하세요:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7
```

## 빌드 및 실행

### Gradle을 이용한 빌드

```bash
./gradlew clean build
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IntelliJ IDEA에서 `McpApplication.java`를 직접 실행할 수 있습니다.

실행 후 서버는 `http://localhost:8080`에서 시작됩니다.

## API 사용법

### 1. 헬스 체크

서버가 정상적으로 실행되는지 확인합니다.

```bash
curl http://localhost:8080/mcp/health
```

응답:
```json
{
  "status": "ok",
  "message": "MCP Server is running"
}
```

### 2. 사용 가능한 도구 목록 조회

```bash
curl http://localhost:8080/mcp/tools
```

응답:
```json
[
  {
    "name": "get_weather",
    "description": "Get the current weather for a location",
    "inputSchema": {
      "type": "object",
      "properties": {
        "location": {
          "type": "string",
          "description": "The city name"
        }
      },
      "required": ["location"]
    }
  },
  {
    "name": "calculate",
    "description": "Perform mathematical calculations",
    "inputSchema": {
      "type": "object",
      "properties": {
        "expression": {
          "type": "string",
          "description": "Mathematical expression to evaluate"
        }
      },
      "required": ["expression"]
    }
  }
]
```

### 3. 도구 실행

#### 날씨 조회

```bash
curl -X POST http://localhost:8080/mcp/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "name": "get_weather",
    "arguments": {
      "location": "Seoul"
    }
  }'
```

응답:
```json
{
  "location": "Seoul",
  "weather": "Seoul is currently experiencing clear skies with a temperature of around 15°C..."
}
```

#### 계산 수행

```bash
curl -X POST http://localhost:8080/mcp/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "name": "calculate",
    "arguments": {
      "expression": "15 * 23 + 47"
    }
  }'
```

응답:
```json
{
  "expression": "15 * 23 + 47",
  "result": "392"
}
```

### 4. AI 채팅

```bash
curl -X POST http://localhost:8080/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "안녕하세요! Spring Boot에 대해 설명해주세요."
  }'
```

응답:
```json
{
  "response": "Spring Boot는 Spring 프레임워크 기반의 애플리케이션을 쉽게 만들 수 있도록 도와주는 도구입니다..."
}
```

### 5. MCP 프로토콜 형식으로 요청

JSON-RPC 2.0 형식을 따르는 요청도 가능합니다:

```bash
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/list",
    "params": {}
  }'
```

## 주요 기능

### 1. McpService 클래스

Spring AI의 `ChatClient`를 활용하여 AI 기능을 제공합니다.

```java
@Service
public class McpService {
    private final ChatClient chatClient;

    // 도구 목록 제공
    public List<McpTool> listTools() { ... }

    // 도구 실행
    public Object callTool(String toolName, Map<String, Object> arguments) { ... }

    // AI 채팅
    public Object chat(String message) { ... }
}
```

### 2. 제공되는 도구

#### get_weather
- 특정 지역의 날씨 정보를 AI를 통해 조회합니다.
- 입력: `location` (문자열)
- 출력: 날씨 정보

#### calculate
- 수학적 표현식을 AI를 통해 계산합니다.
- 입력: `expression` (문자열)
- 출력: 계산 결과

### 3. 확장 가능한 구조

새로운 도구를 추가하려면:

1. `McpService.listTools()`에 도구 정의 추가
2. `McpService.callTool()`에 도구 실행 로직 추가

예시:
```java
// 새로운 도구 정의
tools.add(new McpTool(
    "translate",
    "Translate text to another language",
    translationSchema
));

// 도구 실행 로직
case "translate" -> translate(
    (String) arguments.get("text"),
    (String) arguments.get("targetLanguage")
);
```

## 개발 가이드

### IDE 설정 (IntelliJ IDEA)

1. IntelliJ IDEA에서 프로젝트 열기
2. Gradle 자동 import 대기
3. Lombok 플러그인 설치 및 Annotation Processing 활성화
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - "Enable annotation processing" 체크

### 테스트 작성

`src/test/java` 디렉토리에 테스트 코드를 작성할 수 있습니다:

```java
@SpringBootTest
class McpServiceTest {
    @Autowired
    private McpService mcpService;

    @Test
    void testListTools() {
        List<McpTool> tools = mcpService.listTools();
        assertNotNull(tools);
        assertTrue(tools.size() > 0);
    }
}
```

## 문제 해결

### OpenAI API 키 오류

```
Error: OPENAI_API_KEY environment variable is not set
```

해결방법: 환경 변수 `OPENAI_API_KEY`를 설정하세요.

### 포트 충돌

```
Error: Port 8080 is already in use
```

해결방법: `application.yml`에서 포트 변경:
```yaml
server:
  port: 8081
```

### Lombok 관련 오류

IntelliJ IDEA에서 Lombok 플러그인이 설치되어 있고 Annotation Processing이 활성화되어 있는지 확인하세요.

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring AI 문서](https://docs.spring.io/spring-ai/reference/)
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [OpenAI API](https://platform.openai.com/docs/)

## 라이선스

MIT License
