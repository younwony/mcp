package dev.wony.mcp.tool.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WeatherCategory 테스트")
class WeatherCategoryTest {

    @ParameterizedTest
    @DisplayName("하늘상태 코드를 올바르게 해석한다")
    @CsvSource({
            "1, 맑음",
            "3, 구름많음",
            "4, 흐림"
    })
    void interpretSkyCode(String code, String expected) {
        // when
        String result = WeatherCategory.interpretSkyCode(code);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("강수형태 코드를 올바르게 해석한다")
    @CsvSource({
            "0, 없음",
            "1, 비",
            "2, 비/눈",
            "3, 눈",
            "4, 소나기",
            "5, 빗방울",
            "6, 빗방울눈날림",
            "7, 눈날림"
    })
    void interpretPtyCode(String code, String expected) {
        // when
        String result = WeatherCategory.interpretPtyCode(code);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("풍향 값을 16방위로 올바르게 변환한다")
    @CsvSource({
            "0, N",
            "45, NE",
            "90, E",
            "135, SE",
            "180, S",
            "225, SW",
            "270, W",
            "315, NW",
            "339, NNW",
            "165, SSE"
    })
    void interpretWindDirection(String degree, String expected) {
        // when
        String result = WeatherCategory.interpretWindDirection(degree);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("기온 카테고리의 설명과 단위를 반환한다")
    void getTemperatureInfo() {
        // given
        WeatherCategory category = WeatherCategory.TMP;

        // when & then
        assertThat(category.getDescription()).isEqualTo("1시간 기온");
        assertThat(category.getUnit()).isEqualTo("℃");
    }

    @Test
    @DisplayName("강수확률 카테고리의 설명과 단위를 반환한다")
    void getPrecipitationProbabilityInfo() {
        // given
        WeatherCategory category = WeatherCategory.POP;

        // when & then
        assertThat(category.getDescription()).isEqualTo("강수확률");
        assertThat(category.getUnit()).isEqualTo("%");
    }

    @Test
    @DisplayName("풍속 카테고리의 설명과 단위를 반환한다")
    void getWindSpeedInfo() {
        // given
        WeatherCategory category = WeatherCategory.WSD;

        // when & then
        assertThat(category.getDescription()).isEqualTo("풍속");
        assertThat(category.getUnit()).isEqualTo("m/s");
    }
}
