package dev.wony.mcp.tool.weather;

import dev.wony.mcp.tool.weather.dto.GridCoordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WeatherService 테스트")
class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // 테스트용 서비스 키 (실제 API 호출은 통합 테스트에서 수행)
        weatherService = new WeatherService("");
    }

    @Test
    @DisplayName("WeatherService가 정상적으로 생성된다")
    void createWeatherService() {
        // then
        assertThat(weatherService).isNotNull();
    }

    @Test
    @DisplayName("격자 좌표 변환이 올바르게 동작한다")
    void gridCoordinateConversion() {
        // given - 서울 좌표
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);

        // then
        assertThat(grid.nx()).isEqualTo(60);
        assertThat(grid.ny()).isEqualTo(127);
    }

    @Test
    @DisplayName("서비스 키가 없을 때도 객체 생성이 가능하다")
    void createWithoutServiceKey() {
        // when
        WeatherService service = new WeatherService("");

        // then
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("서비스 키가 있을 때 객체 생성이 가능하다")
    void createWithServiceKey() {
        // given
        String testServiceKey = "test-service-key";

        // when
        WeatherService service = new WeatherService(testServiceKey);

        // then
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("지원 도시 목록을 조회한다")
    void getSupportedCities() {
        // when
        String result = weatherService.getSupportedCities();

        // then
        assertThat(result).isNotNull();
        assertThat(result).contains("서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "제주");
    }

    @Test
    @DisplayName("지원하지 않는 도시 조회 시 에러 메시지를 반환한다")
    void getCurrentWeather_unsupportedCity() {
        // given
        String unsupportedCity = "평양";

        // when
        String result = weatherService.getCurrentWeather(unsupportedCity);

        // then
        assertThat(result).contains("지원하지 않는 도시");
        assertThat(result).contains("서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "제주");
    }

    @Test
    @DisplayName("도시 이름으로 날씨 조회 시 적절한 응답을 받는다 (API 키 없음)")
    void getCurrentWeather_withoutApiKey() {
        // given
        String city = "서울";

        // when
        String result = weatherService.getCurrentWeather(city);

        // then
        assertThat(result).isNotNull();
        // API 키가 없거나 잘못된 경우 에러 메시지가 포함됨
        assertThat(result).containsAnyOf("날씨 조회 중 오류", "날씨 정보를 가져올 수 없습니다", "API 오류");
    }

    @Test
    @DisplayName("모든 지원 도시에 대해 getCurrentWeather 메서드 호출이 가능하다")
    void getCurrentWeather_allSupportedCities() {
        // given
        String[] cities = {"서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "제주"};

        // when & then
        for (String city : cities) {
            String result = weatherService.getCurrentWeather(city);
            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
