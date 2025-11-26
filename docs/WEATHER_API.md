# 기상청 단기예보 조회서비스 API 가이드

이 문서는 기상청 단기예보 조회서비스 API의 사용 방법과 구현 상세를 설명합니다.

## 📋 목차

- [개요](#개요)
- [제공 API](#제공-api)
- [사용 방법](#사용-방법)
- [좌표 변환](#좌표-변환)
- [응답 데이터 해석](#응답-데이터-해석)
- [에러 처리](#에러-처리)
- [예제](#예제)
- [참고 자료](#참고-자료)

## 개요

기상청 단기예보 조회서비스는 한국 기상청에서 제공하는 공공 데이터 API입니다. 이 프로젝트는 MCP 서버를 통해 Claude Desktop에서 기상청 날씨 정보를 조회할 수 있도록 구현되었습니다.

### 주요 특징

- **실시간 관측 데이터**: 매시각 정시 발표되는 초단기실황 정보
- **단기 예보**: 최대 6시간(초단기) ~ 3일(단기) 예보
- **격자 기반**: Lambert Conformal Conic Projection을 사용한 격자 좌표 시스템
- **위경도 자동 변환**: 위도/경도를 입력하면 자동으로 격자 좌표로 변환

## 제공 API

### 1. 초단기실황조회 (getUltraSrtNcst)

현재 시각 기준 실황 정보를 제공합니다.

#### MCP Tool 정의

```java
@Tool(description = "Get current weather observation for a specific latitude/longitude in Korea. Returns real-time weather data including temperature, precipitation, wind, and humidity.")
public String getUltraSrtNcst(
    @ToolParam(description = "Latitude (위도)") double latitude,
    @ToolParam(description = "Longitude (경도)") double longitude
)
```

#### 발표 시각
- **생성 시각**: 매시각 정시 (00:00, 01:00, ..., 23:00)
- **API 제공**: 매시각 10분 이후 (00:10, 01:10, ..., 23:10)
- **업데이트**: 10분마다 최신 정보로 업데이트

#### 제공 요소

| 카테고리 | 설명 | 단위 |
|---------|------|------|
| T1H | 기온 | ℃ |
| RN1 | 1시간 강수량 | mm |
| UUU | 동서바람성분 | m/s |
| VVV | 남북바람성분 | m/s |
| REH | 습도 | % |
| PTY | 강수형태 | 코드값 |
| VEC | 풍향 | deg |
| WSD | 풍속 | m/s |

#### 강수형태 코드

- `0`: 없음
- `1`: 비
- `2`: 비/눈
- `3`: 눈
- `5`: 빗방울
- `6`: 빗방울눈날림
- `7`: 눈날림

#### 응답 예시

```
=== 초단기실황 (위도: 37.5665, 경도: 126.9780) ===
발표시각: 20241126 1400

기온: 15.2℃
1시간 강수량: 0mm
습도: 65%
강수형태: 없음
풍향: N
풍속: 2.3m/s
동서바람성분: 0.5m/s
남북바람성분: 1.2m/s
```

### 2. 초단기예보조회 (getUltraSrtFcst)

현재 시각부터 6시간 이내의 예보 정보를 제공합니다.

#### MCP Tool 정의

```java
@Tool(description = "Get ultra short-term weather forecast (up to 6 hours) for a specific latitude/longitude in Korea. Returns hourly forecast data.")
public String getUltraSrtFcst(
    @ToolParam(description = "Latitude (위도)") double latitude,
    @ToolParam(description = "Longitude (경도)") double longitude
)
```

#### 발표 시각
- **생성 시각**: 매시각 30분 (00:30, 01:30, ..., 23:30)
- **API 제공**: 매시각 45분 이후 (00:45, 01:45, ..., 23:45)
- **예보 기간**: 발표 시각부터 6시간

#### 제공 요소

초단기실황 요소 + 추가 요소:

| 카테고리 | 설명 | 단위 |
|---------|------|------|
| SKY | 하늘상태 | 코드값 |
| LGT | 낙뢰 | kA |

#### 하늘상태 코드

- `1`: 맑음
- `3`: 구름많음
- `4`: 흐림

#### 응답 예시

```
=== 초단기예보 (위도: 37.5665, 경도: 126.9780) ===
발표시각: 20241126 1430

[20241126 1500]
  기온: 15.0℃
  1시간 강수량: 0mm
  하늘상태: 맑음
  강수형태: 없음
  습도: 60%
  풍향: NE
  풍속: 2.5m/s

[20241126 1600]
  기온: 14.5℃
  1시간 강수량: 0mm
  하늘상태: 맑음
  강수형태: 없음
  ...
```

### 3. 단기예보조회 (getVilageFcst)

최대 3일(72시간) 예보 정보를 제공합니다.

#### MCP Tool 정의

```java
@Tool(description = "Get short-term weather forecast (up to 3 days) for a specific latitude/longitude in Korea. Returns detailed forecast including temperature, precipitation, wind, and sky conditions.")
public String getVilageFcst(
    @ToolParam(description = "Latitude (위도)") double latitude,
    @ToolParam(description = "Longitude (경도)") double longitude
)
```

#### 발표 시각
- **발표 시각**: 02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00 (하루 8회)
- **API 제공**: 각 발표 시각 10분 이후
- **예보 기간**:
  - 02·05·08·11·14시 발표: 오늘 ~ +4일
  - 17·20·23시 발표: 오늘 ~ +5일

#### 제공 요소

| 카테고리 | 설명 | 단위 |
|---------|------|------|
| POP | 강수확률 | % |
| PTY | 강수형태 | 코드값 |
| PCP | 1시간 강수량 | mm |
| REH | 습도 | % |
| SNO | 1시간 신적설 | cm |
| SKY | 하늘상태 | 코드값 |
| TMP | 1시간 기온 | ℃ |
| TMN | 일 최저기온 | ℃ |
| TMX | 일 최고기온 | ℃ |
| UUU | 풍속(동서성분) | m/s |
| VVV | 풍속(남북성분) | m/s |
| WAV | 파고 | M |
| VEC | 풍향 | deg |
| WSD | 풍속 | m/s |

#### 강수형태 코드 (단기예보)

- `0`: 없음
- `1`: 비
- `2`: 비/눈
- `3`: 눈
- `4`: 소나기

#### 응답 예시

```
=== 단기예보 (위도: 37.5665, 경도: 126.9780) ===
발표시각: 20241126 1100

[20241126 1200]
  1시간 기온: 16.0℃
  강수확률: 20%
  하늘상태: 구름많음
  1시간 강수량: 0mm
  습도: 55%
  풍속: 3.2m/s
  ...

[20241126 1300]
  1시간 기온: 17.0℃
  ...
```

## 사용 방법

### Claude Desktop에서 사용

#### 1. 초단기실황 조회

```
서울(위도 37.5665, 경도 126.9780)의 현재 날씨를 알려줘
```

또는

```
부산의 지금 날씨는 어때?
```

#### 2. 초단기예보 조회

```
제주도의 앞으로 6시간 날씨 예보를 알려줘
```

#### 3. 단기예보 조회

```
대전의 3일간 날씨 예보를 보여줘
```

### 프로그래밍 방식

```java
@Autowired
private WeatherService weatherService;

// 초단기실황
String current = weatherService.getUltraSrtNcst(37.5665, 126.9780);

// 초단기예보
String shortForecast = weatherService.getUltraSrtFcst(37.5665, 126.9780);

// 단기예보
String forecast = weatherService.getVilageFcst(37.5665, 126.9780);
```

## 좌표 변환

기상청 API는 격자 좌표(nx, ny)를 사용합니다. 이 프로젝트는 위경도를 자동으로 격자 좌표로 변환합니다.

### Lambert Conformal Conic Projection

기상청은 Lambert Conformal Conic Projection 방식을 사용합니다.

#### 변환 파라미터

```java
final double RE = 6371.00877;    // 지구 반경 (km)
final double GRID = 5.0;          // 격자 간격 (km)
final double SLAT1 = 30.0;        // 표준위도 1
final double SLAT2 = 60.0;        // 표준위도 2
final double OLON = 126.0;        // 기준점 경도
final double OLAT = 38.0;         // 기준점 위도
final double XO = 210.0 / GRID;   // 기준점 X좌표
final double YO = 675.0 / GRID;   // 기준점 Y좌표
```

### 주요 도시 격자 좌표

| 도시 | 위도 | 경도 | nx | ny |
|------|------|------|----|----|
| 서울 | 37.5665 | 126.9780 | 60 | 127 |
| 부산 | 35.1796 | 129.0756 | 98 | 76 |
| 제주 | 33.4996 | 126.5312 | 53 | 38 |
| 대전 | 36.3504 | 127.3845 | 67 | 100 |
| 대구 | 35.8714 | 128.6014 | 89 | 90 |
| 광주 | 35.1595 | 126.8526 | 58 | 74 |
| 인천 | 37.4563 | 126.7052 | 55 | 124 |

### 좌표 변환 사용

```java
// 위경도를 격자 좌표로 변환
GridCoordinate grid = GridCoordinate.fromLatLon(37.5665, 126.9780);
System.out.println("nx: " + grid.nx());  // 60
System.out.println("ny: " + grid.ny());  // 127
```

전국의 모든 격자 좌표는 `src/main/resources/doc/weather/기상청_격자_위경도` 파일에서 확인할 수 있습니다.

## 응답 데이터 해석

### 풍향 16방위

풍향 값(degree)을 16방위로 변환합니다.

```java
int index = (int) ((degree + 22.5 * 0.5) / 22.5);
String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                       "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
return directions[index % 16];
```

#### 방위표

| 범위(°) | 방위 | 한글 |
|---------|------|------|
| 0-22.5, 337.5-360 | N | 북 |
| 22.5-67.5 | NE | 북동 |
| 67.5-112.5 | E | 동 |
| 112.5-157.5 | SE | 남동 |
| 157.5-202.5 | S | 남 |
| 202.5-247.5 | SW | 남서 |
| 247.5-292.5 | W | 서 |
| 292.5-337.5 | NW | 북서 |

### 강수량 범주

#### 초단기예보 및 단기예보

| 범주 | 표시 |
|------|------|
| 0.1 ~ 1.0mm 미만 | 1mm 미만 |
| 1.0mm 이상 30.0mm 미만 | 실수값+mm (예: 6.2mm) |
| 30.0mm 이상 50.0mm 미만 | 30.0~50.0mm |
| 50.0mm 이상 | 50.0mm 이상 |

### 적설량 범주

| 범주 | 표시 |
|------|------|
| 0.1 ~ 0.5cm 미만 | 0.5cm 미만 |
| 0.5cm 이상 5.0cm 미만 | 실수값+cm (예: 2.3cm) |
| 5.0cm 이상 | 5.0cm 이상 |

## 에러 처리

### API 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| 00 | NORMAL_SERVICE | 정상 |
| 01 | APPLICATION_ERROR | 애플리케이션 에러 |
| 02 | DB_ERROR | 데이터베이스 에러 |
| 03 | NODATA_ERROR | 데이터없음 에러 |
| 10 | INVALID_REQUEST_PARAMETER_ERROR | 잘못된 요청 파라미터 |
| 11 | NO_MANDATORY_REQUEST_PARAMETERS_ERROR | 필수 파라미터 누락 |
| 20 | SERVICE_ACCESS_DENIED_ERROR | 서비스 접근 거부 |
| 30 | SERVICE_KEY_IS_NOT_REGISTERED_ERROR | 등록되지 않은 서비스키 |
| 31 | DEADLINE_HAS_EXPIRED_ERROR | 기한만료된 서비스키 |
| 32 | UNREGISTERED_IP_ERROR | 등록되지 않은 IP |

### 에러 처리 예시

```java
try {
    String weather = weatherService.getUltraSrtNcst(latitude, longitude);
    return weather;
} catch (RestClientException e) {
    return String.format("날씨 정보 조회 실패: %s", e.getMessage());
}
```

응답에서 에러 확인:

```java
String resultCode = response.response().header().resultCode();
if (!"00".equals(resultCode)) {
    return String.format("API 오류: %s - %s",
            resultCode, response.response().header().resultMsg());
}
```

## 예제

### 예제 1: 특정 위치의 현재 날씨

```java
WeatherService service = new WeatherService("YOUR_API_KEY");
String weather = service.getUltraSrtNcst(37.5665, 126.9780);
System.out.println(weather);
```

출력:
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

### 예제 2: 6시간 예보

```java
String forecast = service.getUltraSrtFcst(35.1796, 129.0756); // 부산
System.out.println(forecast);
```

### 예제 3: 3일 예보

```java
String longForecast = service.getVilageFcst(33.4996, 126.5312); // 제주
System.out.println(longForecast);
```

### 예제 4: 좌표 변환

```java
// 서울의 위경도를 격자 좌표로 변환
GridCoordinate seoul = GridCoordinate.fromLatLon(37.5665, 126.9780);
System.out.println("서울 격자: (" + seoul.nx() + ", " + seoul.ny() + ")");
// 출력: 서울 격자: (60, 127)
```

## 설정

### API 키 설정

`application.yml` 또는 환경 변수로 API 키를 설정합니다.

#### application.yml

```yaml
weather:
  api:
    service-key: YOUR_API_KEY_HERE
```

#### 환경 변수

```bash
export WEATHER_API_SERVICE_KEY=YOUR_API_KEY_HERE
```

### API 키 발급

1. [공공데이터포털](https://www.data.go.kr/) 접속
2. 회원가입 및 로그인
3. "기상청_단기예보 조회서비스" 검색
4. 활용신청
5. 발급받은 인증키를 설정에 추가

## 테스트

### 단위 테스트

```bash
./gradlew test
```

### 테스트 커버리지

현재 32개의 테스트가 구현되어 있습니다:

- **GridCoordinateTest**: 좌표 변환 검증 (4개)
- **WeatherCategoryTest**: 코드 해석 검증 (6개)
- **WeatherServiceTest**: API 통합 테스트 (22개)

## 참고 자료

### 공식 문서

- [기상청 단기예보 조회서비스](https://www.data.go.kr/data/15084084/openapi.do)
- [공공데이터포털](https://www.data.go.kr/)

### 프로젝트 내 문서

- `src/main/resources/doc/weather/기상청_API`: API 활용 가이드 전문
- `src/main/resources/doc/weather/기상청_격자_위경도`: 전국 격자 좌표 데이터
- `src/main/resources/doc/weather/기상청41_단기예보 조회서비스_오픈API활용가이드_241128.docx`: 상세 매뉴얼

### 구현 코드

- `WeatherService.java`: 메인 서비스 로직
- `GridCoordinate.java`: 좌표 변환 구현
- `WeatherCategory.java`: 예보 요소 및 코드 해석
- `WeatherApiResponse.java`: API 응답 DTO

## 제한 사항

1. **지리적 범위**: 남한 지역만 지원 (북한 및 국외 미지원)
2. **격자 해상도**: 5km × 5km 격자
3. **API 호출 제한**:
   - 일반 인증키: 1일 1,000회
   - 상세한 제한은 공공데이터포털 참조
4. **데이터 지연**:
   - 초단기실황: 발표 시각 + 10분
   - 초단기예보: 발표 시각 + 15분
   - 단기예보: 발표 시각 + 10분

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 기상청 데이터는 [공공누리 제1유형](https://www.kogl.or.kr/info/license.do#01-tab)으로 출처 표시 시 자유 이용 가능합니다.

---

**데이터 출처**: 기상청 단기예보 조회서비스 (공공데이터포털)
