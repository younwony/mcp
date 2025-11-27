# 기상청 단기예보 조회서비스 API 가이드

## 개요

기상청에서 제공하는 단기예보 조회서비스는 전국 지역의 기상 예보 정보를 제공하는 REST API입니다.

**공공데이터포털**: https://www.data.go.kr/
**서비스명**: 기상청_단기예보 ((구)_동네예보) 조회서비스

## 주요 API 목록

### 1. 초단기실황조회 (getUltraSrtNcst)

**설명**: 현재 시각 기준 실황 정보 제공
**발표주기**: 매시간 정시 발표 (10분 후 API 제공)
**제공항목**:
- T1H: 기온 (°C)
- RN1: 1시간 강수량 (mm)
- UUU: 동서바람성분 (m/s)
- VVV: 남북바람성분 (m/s)
- REH: 습도 (%)
- PTY: 강수형태 (코드)
- VEC: 풍향 (deg)
- WSD: 풍속 (m/s)
- LGT: 낙뢰 (kA)

**호출 예시**:
```
GET /getUltraSrtNcst?serviceKey={서비스키}&base_date=20231126&base_time=1200&nx=60&ny=127
```

### 2. 초단기예보조회 (getUltraSrtFcst)

**설명**: 6시간 이내 예보 정보 제공
**발표주기**: 매시간 30분 발표 (45분 후 API 제공)
**예보기간**: 발표시각 기준 +6시간
**제공항목**:
- T1H: 기온 (°C)
- RN1: 1시간 강수량 (mm)
- SKY: 하늘상태 (코드)
- UUU: 동서바람성분 (m/s)
- VVV: 남북바람성분 (m/s)
- REH: 습도 (%)
- PTY: 강수형태 (코드)
- LGT: 낙뢰 (kA)
- VEC: 풍향 (deg)
- WSD: 풍속 (m/s)

**호출 예시**:
```
GET /getUltraSrtFcst?serviceKey={서비스키}&base_date=20231126&base_time=1230&nx=60&ny=127
```

### 3. 단기예보조회 (getVilageFcst)

**설명**: 3일간 예보 정보 제공
**발표주기**: 하루 8회 (02, 05, 08, 11, 14, 17, 20, 23시) + 10분 후 API 제공
**예보기간**: 발표시각 기준 +3일
**제공항목**:
- POP: 강수확률 (%)
- PTY: 강수형태 (코드)
- PCP: 1시간 강수량 (mm)
- REH: 습도 (%)
- SNO: 1시간 신적설 (cm)
- SKY: 하늘상태 (코드)
- TMP: 1시간 기온 (°C)
- TMN: 일 최저기온 (°C)
- TMX: 일 최고기온 (°C)
- UUU: 동서바람성분 (m/s)
- VVV: 남북바람성분 (m/s)
- WAV: 파고 (m)
- VEC: 풍향 (deg)
- WSD: 풍속 (m/s)

**호출 예시**:
```
GET /getVilageFcst?serviceKey={서비스키}&base_date=20231126&base_time=0500&nx=60&ny=127
```

## 좌표 체계

### Lambert Conformal Conic Projection

기상청 API는 위경도 좌표가 아닌 격자 좌표(nx, ny)를 사용합니다.

**투영 파라미터**:
- 지구 반경: 6371.00877 km
- 격자 간격: 5.0 km
- 투영 위도1: 30.0°
- 투영 위도2: 60.0°
- 기준점 경도: 126.0°
- 기준점 위도: 38.0°
- 기준점 X좌표: 43
- 기준점 Y좌표: 136

### 주요 도시 격자 좌표

| 도시 | 위도 | 경도 | nx | ny |
|------|------|------|-----|-----|
| 서울 | 37.5665 | 126.9780 | 60 | 127 |
| 부산 | 35.1796 | 129.0756 | 98 | 76 |
| 대구 | 35.8714 | 128.6014 | 89 | 90 |
| 인천 | 37.4563 | 126.7052 | 55 | 124 |
| 광주 | 35.1595 | 126.8526 | 58 | 74 |
| 대전 | 36.3504 | 127.3845 | 67 | 100 |
| 울산 | 35.5384 | 129.3114 | 102 | 84 |
| 제주 | 33.4996 | 126.5312 | 52 | 38 |

**격자 좌표 엑셀**: `기상청41_단기예보 조회서비스_오픈API활용가이드_격자_위경도(2510).xlsx`

## 코드값 정의

### 하늘상태 (SKY)

| 코드 | 설명 |
|------|------|
| 1 | 맑음 |
| 3 | 구름많음 |
| 4 | 흐림 |

### 강수형태 (PTY)

**초단기실황 (Ultra Short Range Nowcast)**:
| 코드 | 설명 |
|------|------|
| 0 | 없음 |
| 1 | 비 |
| 2 | 비/눈 |
| 3 | 눈 |
| 5 | 빗방울 |
| 6 | 빗방울눈날림 |
| 7 | 눈날림 |

**단기예보 (Short Range Forecast)**:
| 코드 | 설명 |
|------|------|
| 0 | 없음 |
| 1 | 비 |
| 2 | 비/눈 |
| 3 | 눈 |
| 4 | 소나기 |

## 요청 파라미터

### 공통 파라미터

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|----------|------|------|------|------|
| serviceKey | String | O | 인증키 | 공공데이터포털에서 발급 |
| pageNo | Integer | O | 페이지 번호 | 1 |
| numOfRows | Integer | O | 한 페이지 결과 수 | 10 |
| dataType | String | O | 응답 자료 형식 | JSON, XML |
| base_date | String | O | 발표일자 | 20231126 (yyyyMMdd) |
| base_time | String | O | 발표시각 | 0600 (HHmm) |
| nx | Integer | O | 예보지점 X 좌표 | 60 |
| ny | Integer | O | 예보지점 Y 좌표 | 127 |

### 시간 계산 주의사항

1. **초단기실황 (getUltraSrtNcst)**
   - 발표: 매시간 00분 (정시)
   - API 제공: 발표 후 10분
   - 예) 12:00 발표 → 12:10부터 API 조회 가능
   - 권장: 현재 시각 기준 40분 이전이면 이전 시간 사용

2. **초단기예보 (getUltraSrtFcst)**
   - 발표: 매시간 30분
   - API 제공: 발표 후 15분 (45분)
   - 예) 12:30 발표 → 12:45부터 API 조회 가능
   - 권장: 현재 시각 기준 45분 이전이면 이전 시간 사용

3. **단기예보 (getVilageFcst)**
   - 발표: 02, 05, 08, 11, 14, 17, 20, 23시
   - API 제공: 발표 후 10분
   - 예) 02:00 발표 → 02:10부터 API 조회 가능
   - 권장: 가장 최근 발표시각 사용

## 응답 형식

### JSON 응답 구조

```json
{
  "response": {
    "header": {
      "resultCode": "00",
      "resultMsg": "NORMAL_SERVICE"
    },
    "body": {
      "dataType": "JSON",
      "items": {
        "item": [
          {
            "baseDate": "20231126",
            "baseTime": "1200",
            "category": "T1H",
            "fcstDate": "20231126",
            "fcstTime": "1300",
            "fcstValue": "15",
            "nx": 60,
            "ny": 127
          }
        ]
      },
      "pageNo": 1,
      "numOfRows": 10,
      "totalCount": 60
    }
  }
}
```

### 응답 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| 00 | NORMAL_SERVICE | 정상 |
| 01 | APPLICATION_ERROR | 어플리케이션 에러 |
| 02 | DB_ERROR | 데이터베이스 에러 |
| 03 | NODATA_ERROR | 데이터없음 에러 |
| 04 | HTTP_ERROR | HTTP 에러 |
| 05 | SERVICETIMEOUT_ERROR | 서비스 연결실패 에러 |
| 10 | INVALID_REQUEST_PARAMETER_ERROR | 잘못된 요청 파라미터 에러 |
| 11 | NO_MANDATORY_REQUEST_PARAMETERS_ERROR | 필수요청 파라미터가 없음 |
| 12 | NO_OPENAPI_SERVICE_ERROR | 해당 오픈API서비스가 없거나 폐기됨 |
| 20 | SERVICE_ACCESS_DENIED_ERROR | 서비스 접근거부 |
| 22 | LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR | 서비스 요청제한횟수 초과에러 |
| 30 | SERVICE_KEY_IS_NOT_REGISTERED_ERROR | 등록되지 않은 서비스키 |
| 31 | DEADLINE_HAS_EXPIRED_ERROR | 기한만료된 서비스키 |
| 32 | UNREGISTERED_IP_ERROR | 등록되지 않은 IP |
| 33 | UNSIGNED_CALL_ERROR | 서명되지 않은 호출 |

## 구현 권장사항

### 1. 에러 처리
- 응답 코드 검증
- 재시도 로직 (일시적 오류)
- 타임아웃 설정

### 2. 성능 최적화
- 캐싱 적용 (동일 시간대 중복 호출 방지)
- 비동기 처리
- Connection Pool 사용

### 3. 데이터 검증
- 필수 파라미터 검증
- 좌표 범위 검증 (한국 영역)
- 시간 형식 검증

### 4. 로깅
- API 호출 이력
- 에러 로그
- 응답 시간 모니터링

## 참고 문서

- **활용 가이드**: `기상청41_단기예보 조회서비스_오픈API활용가이드_241128.docx`
- **격자 좌표**: `기상청41_단기예보 조회서비스_오픈API활용가이드_격자_위경도(2510).xlsx`
- **공공데이터포털**: https://www.data.go.kr/

## 라이선스

공공누리 제1유형: 출처표시
기상청에서 작성한 저작물은 "공공누리 제1유형: 출처표시" 조건에 따라 이용할 수 있습니다.
