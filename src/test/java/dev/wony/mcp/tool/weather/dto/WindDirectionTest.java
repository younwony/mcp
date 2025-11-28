package dev.wony.mcp.tool.weather.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("WindDirection 값 객체 테스트")
class WindDirectionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 각도로 WindDirection을 생성할 수 있다")
        void shouldCreateWindDirection_whenValidDegree() {
            // given
            double validDegree = 90.0;

            // when
            WindDirection windDirection = new WindDirection(validDegree);

            // then
            assertThat(windDirection.degree()).isEqualTo(validDegree);
        }

        @Test
        @DisplayName("0도는 유효한 각도이다")
        void shouldCreateWindDirection_whenZeroDegree() {
            // given
            double zeroDegree = 0.0;

            // when
            WindDirection windDirection = new WindDirection(zeroDegree);

            // then
            assertThat(windDirection.degree()).isEqualTo(zeroDegree);
        }

        @Test
        @DisplayName("음수 각도는 예외가 발생한다")
        void shouldThrowException_whenNegativeDegree() {
            // given
            double negativeDegree = -1.0;

            // when & then
            assertThatThrownBy(() -> new WindDirection(negativeDegree))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("풍향은");
        }

        @Test
        @DisplayName("360도 이상은 예외가 발생한다")
        void shouldThrowException_whenDegreeOverLimit() {
            // given
            double overDegree = 360.0;

            // when & then
            assertThatThrownBy(() -> new WindDirection(overDegree))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("풍향은");
        }
    }

    @Nested
    @DisplayName("16방위 변환 테스트")
    class CompassDirectionTest {

        @ParameterizedTest(name = "{0}도는 {1} 방향이다")
        @CsvSource({
                "0, N",
                "22.5, NNE",
                "45, NE",
                "67.5, ENE",
                "90, E",
                "112.5, ESE",
                "135, SE",
                "157.5, SSE",
                "180, S",
                "202.5, SSW",
                "225, SW",
                "247.5, WSW",
                "270, W",
                "292.5, WNW",
                "315, NW",
                "337.5, NNW",
                "359, N"
        })
        @DisplayName("각도를 16방위로 정확하게 변환한다")
        void shouldConvertToCompassDirection_correctly(double degree, String expected) {
            // given
            WindDirection windDirection = new WindDirection(degree);

            // when
            String compassDirection = windDirection.toCompassDirection();

            // then
            assertThat(compassDirection).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("한글 방위 변환 테스트")
    class KoreanDirectionTest {

        @ParameterizedTest(name = "{0}도는 {1} 방향이다")
        @CsvSource({
                "0, 북",
                "45, 북동",
                "90, 동",
                "135, 남동",
                "180, 남",
                "225, 남서",
                "270, 서",
                "315, 북서"
        })
        @DisplayName("각도를 한글 방위로 정확하게 변환한다")
        void shouldConvertToKoreanDirection_correctly(double degree, String expected) {
            // given
            WindDirection windDirection = new WindDirection(degree);

            // when
            String koreanDirection = windDirection.toKoreanDirection();

            // then
            assertThat(koreanDirection).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("문자열 파싱 테스트")
    class FromStringTest {

        @Test
        @DisplayName("문자열을 WindDirection으로 변환할 수 있다")
        void shouldParseStringToDegree() {
            // given
            String degreeStr = "90.5";

            // when
            WindDirection windDirection = WindDirection.from(degreeStr);

            // then
            assertThat(windDirection.degree()).isEqualTo(90.5);
        }

        @Test
        @DisplayName("유효하지 않은 문자열은 예외가 발생한다")
        void shouldThrowException_whenInvalidString() {
            // given
            String invalidStr = "invalid";

            // when & then
            assertThatThrownBy(() -> WindDirection.from(invalidStr))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("각도와 방위를 포함한 문자열을 반환한다")
        void shouldReturnFormattedString() {
            // given
            WindDirection windDirection = new WindDirection(90.0);

            // when
            String result = windDirection.toString();

            // then
            assertThat(result).contains("90.0");
            assertThat(result).contains("E");
        }
    }
}
