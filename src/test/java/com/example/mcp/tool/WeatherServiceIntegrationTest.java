package com.example.mcp.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WeatherService 실제 API 호출 통합 테스트
 *
 * 이 테스트는 실제 기상청 API를 호출하므로:
 * - API 키가 필요합니다
 * - 네트워크 연결이 필요합니다
 * - API 호출 제한이 적용됩니다
 *
 * 실행 방법:
 * ./gradlew test --tests WeatherServiceIntegrationTest
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
@Tag("integration")
@DisplayName("WeatherService 실제 API 호출 통합 테스트")
class WeatherServiceIntegrationTest {

    @Value("${weather.api.service-key}")
    private String serviceKey;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(serviceKey);
    }

    @Test
    @DisplayName("서울의 초단기실황 조회가 정상 동작한다")
    void getUltraSrtNcst_Seoul() {
        // given - 서울 좌표
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        String result = weatherService.getUltraSrtNcst(latitude, longitude);

        // then
        assertThat(result).isNotNull();
        assertThat(result).contains("초단기실황");
        assertThat(result).contains("위도: 37.5665");
        assertThat(result).contains("경도: 126.9780");
        assertThat(result).contains("발표시각:");

        // 주요 예보 요소 확인
        assertThat(result).containsAnyOf("기온:", "℃");
        assertThat(result).containsAnyOf("습도:", "%");
        assertThat(result).containsAnyOf("풍속:", "m/s");

        // 에러 메시지가 없는지 확인
        assertThat(result).doesNotContain("실패");
        assertThat(result).doesNotContain("오류");
        assertThat(result).doesNotContain("ERROR");

        System.out.println("\n=== 서울 초단기실황 ===");
        System.out.println(result);
    }

    @Test
    @DisplayName("부산의 초단기예보 조회가 정상 동작한다")
    void getUltraSrtFcst_Busan() {
        // given - 부산 좌표
        double latitude = 35.1796;
        double longitude = 129.0756;

        // when
        String result = weatherService.getUltraSrtFcst(latitude, longitude);

        // then
        assertThat(result).isNotNull();
        assertThat(result).contains("초단기예보");
        assertThat(result).contains("위도: 35.1796");
        assertThat(result).contains("경도: 129.0756");
        assertThat(result).contains("발표시각:");

        // 시간대별 예보 확인
        assertThat(result).matches("(?s).*\\[\\d{8} \\d{4}\\].*");  // [YYYYMMDD HHMM] 형식

        // 주요 예보 요소 확인
        assertThat(result).containsAnyOf("기온:", "℃");
        assertThat(result).containsAnyOf("하늘상태:", "맑음", "구름많음", "흐림");

        assertThat(result).doesNotContain("실패");
        assertThat(result).doesNotContain("오류");

        System.out.println("\n=== 부산 초단기예보 ===");
        System.out.println(result);
    }

    @Test
    @DisplayName("제주의 단기예보 조회가 정상 동작한다")
    void getVilageFcst_Jeju() {
        // given - 제주 좌표
        double latitude = 33.4996;
        double longitude = 126.5312;

        // when
        String result = weatherService.getVilageFcst(latitude, longitude);

        // then
        assertThat(result).isNotNull();
        assertThat(result).contains("단기예보");
        assertThat(result).contains("위도: 33.4996");
        assertThat(result).contains("경도: 126.5312");
        assertThat(result).contains("발표시각:");

        // 시간대별 예보 확인
        assertThat(result).matches("(?s).*\\[\\d{8} \\d{4}\\].*");

        // 단기예보 특화 요소 확인
        assertThat(result).containsAnyOf("강수확률:", "%");

        assertThat(result).doesNotContain("실패");
        assertThat(result).doesNotContain("오류");

        System.out.println("\n=== 제주 단기예보 ===");
        System.out.println(result);
    }

    @Test
    @DisplayName("대전의 모든 API가 정상 동작한다")
    void allApis_Daejeon() {
        // given - 대전 좌표
        double latitude = 36.3504;
        double longitude = 127.3845;

        // when - 초단기실황
        String ncst = weatherService.getUltraSrtNcst(latitude, longitude);

        // when - 초단기예보
        String fcst = weatherService.getUltraSrtFcst(latitude, longitude);

        // when - 단기예보
        String vilageFcst = weatherService.getVilageFcst(latitude, longitude);

        // then - 모두 정상 응답
        assertThat(ncst).isNotNull().contains("초단기실황").doesNotContain("실패");
        assertThat(fcst).isNotNull().contains("초단기예보").doesNotContain("실패");
        assertThat(vilageFcst).isNotNull().contains("단기예보").doesNotContain("실패");

        System.out.println("\n=== 대전 전체 API 테스트 ===");
        System.out.println("1. 초단기실황:");
        System.out.println(ncst);
        System.out.println("\n2. 초단기예보:");
        System.out.println(fcst);
        System.out.println("\n3. 단기예보:");
        System.out.println(vilageFcst);
    }

    @Test
    @DisplayName("API 키 유효성 검증")
    void validateServiceKey() {
        // then
        assertThat(serviceKey).isNotNull();
        assertThat(serviceKey).isNotEmpty();
        assertThat(serviceKey).isNotEqualTo("YOUR_API_KEY_HERE");

        System.out.println("API 키 길이: " + serviceKey.length());
        System.out.println("API 키 앞 10자: " + serviceKey.substring(0, Math.min(10, serviceKey.length())) + "...");
    }

    @Test
    @DisplayName("응답 시간 측정 - 초단기실황")
    void measureResponseTime_UltraSrtNcst() {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        long startTime = System.currentTimeMillis();
        String result = weatherService.getUltraSrtNcst(latitude, longitude);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // then
        assertThat(result).isNotNull();
        assertThat(responseTime).isLessThan(10000); // 10초 이내

        System.out.println("초단기실황 응답 시간: " + responseTime + "ms");
    }

    @Test
    @DisplayName("응답 시간 측정 - 초단기예보")
    void measureResponseTime_UltraSrtFcst() {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        long startTime = System.currentTimeMillis();
        String result = weatherService.getUltraSrtFcst(latitude, longitude);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // then
        assertThat(result).isNotNull();
        assertThat(responseTime).isLessThan(10000); // 10초 이내

        System.out.println("초단기예보 응답 시간: " + responseTime + "ms");
    }

    @Test
    @DisplayName("응답 시간 측정 - 단기예보")
    void measureResponseTime_VilageFcst() {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        long startTime = System.currentTimeMillis();
        String result = weatherService.getVilageFcst(latitude, longitude);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // then
        assertThat(result).isNotNull();
        assertThat(responseTime).isLessThan(15000); // 15초 이내 (단기예보는 데이터가 많음)

        System.out.println("단기예보 응답 시간: " + responseTime + "ms");
    }

    @Test
    @DisplayName("다양한 지역에 대한 연속 호출 테스트")
    void multipleLocations() {
        // given - 여러 주요 도시
        double[][] locations = {
            {37.5665, 126.9780},  // 서울
            {35.1796, 129.0756},  // 부산
            {33.4996, 126.5312},  // 제주
            {36.3504, 127.3845},  // 대전
            {35.8714, 128.6014}   // 대구
        };
        String[] cities = {"서울", "부산", "제주", "대전", "대구"};

        // when & then
        for (int i = 0; i < locations.length; i++) {
            String result = weatherService.getUltraSrtNcst(locations[i][0], locations[i][1]);

            assertThat(result).isNotNull();
            assertThat(result).contains("초단기실황");
            assertThat(result).doesNotContain("실패");

            System.out.println(cities[i] + " 조회 성공: ✓");
        }
    }
}
