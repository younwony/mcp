package com.example.mcp.tool;

import com.example.mcp.tool.dto.GridCoordinate;
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
}
