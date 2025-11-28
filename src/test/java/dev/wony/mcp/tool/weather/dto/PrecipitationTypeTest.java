package dev.wony.mcp.tool.weather.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PrecipitationType enum 테스트")
class PrecipitationTypeTest {

    @Nested
    @DisplayName("코드로 PrecipitationType 찾기")
    class FromCodeTest {

        @ParameterizedTest(name = "코드 {0}은 {1}이다")
        @CsvSource({
                "0, NONE",
                "1, RAIN",
                "2, RAIN_SNOW",
                "3, SNOW",
                "4, SHOWER",
                "5, DRIZZLE",
                "6, SLEET",
                "7, SNOW_DRIFT"
        })
        @DisplayName("유효한 코드로 PrecipitationType을 찾을 수 있다")
        void shouldFindPrecipitationType_whenValidCode(String code, PrecipitationType expected) {
            // when
            PrecipitationType result = PrecipitationType.fromCode(code);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("유효하지 않은 코드는 예외가 발생한다")
        void shouldThrowException_whenInvalidCode() {
            // given
            String invalidCode = "99";

            // when & then
            assertThatThrownBy(() -> PrecipitationType.fromCode(invalidCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }
    }

    @Nested
    @DisplayName("코드 해석 테스트")
    class InterpretTest {

        @ParameterizedTest(name = "코드 {0}은 {1}로 해석된다")
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
        @DisplayName("코드를 설명 문자열로 변환한다")
        void shouldInterpretCode_correctly(String code, String expected) {
            // when
            String result = PrecipitationType.interpret(code);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("유효하지 않은 코드는 원본 코드를 반환한다")
        void shouldReturnOriginalCode_whenInvalidCode() {
            // given
            String invalidCode = "99";

            // when
            String result = PrecipitationType.interpret(invalidCode);

            // then
            assertThat(result).isEqualTo(invalidCode);
        }
    }

    @Nested
    @DisplayName("강수 여부 확인 테스트")
    class HasPrecipitationTest {

        @Test
        @DisplayName("NONE은 강수가 없다")
        void shouldReturnFalse_forNone() {
            // when
            boolean result = PrecipitationType.NONE.hasPrecipitation();

            // then
            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @EnumSource(value = PrecipitationType.class, names = {"RAIN", "RAIN_SNOW", "SNOW", "SHOWER", "DRIZZLE", "SLEET", "SNOW_DRIFT"})
        @DisplayName("NONE이 아니면 강수가 있다")
        void shouldReturnTrue_forPrecipitation(PrecipitationType type) {
            // when
            boolean result = type.hasPrecipitation();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("속성 접근 테스트")
    class PropertyTest {

        @Test
        @DisplayName("RAIN의 속성을 확인한다")
        void shouldHaveCorrectProperties_forRain() {
            // given
            PrecipitationType rain = PrecipitationType.RAIN;

            // when & then
            assertThat(rain.getCode()).isEqualTo("1");
            assertThat(rain.getDescription()).isEqualTo("비");
            assertThat(rain.getEmoji()).isNotEmpty();
        }

        @Test
        @DisplayName("NONE의 이모지는 비어있다")
        void shouldHaveEmptyEmoji_forNone() {
            // given
            PrecipitationType none = PrecipitationType.NONE;

            // when & then
            assertThat(none.getEmoji()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("이모지가 있으면 이모지와 설명을 반환한다")
        void shouldReturnFormattedString_whenHasEmoji() {
            // given
            PrecipitationType rain = PrecipitationType.RAIN;

            // when
            String result = rain.toString();

            // then
            assertThat(result).contains(rain.getEmoji());
            assertThat(result).contains(rain.getDescription());
        }

        @Test
        @DisplayName("이모지가 없으면 설명만 반환한다")
        void shouldReturnDescriptionOnly_whenNoEmoji() {
            // given
            PrecipitationType none = PrecipitationType.NONE;

            // when
            String result = none.toString();

            // then
            assertThat(result).isEqualTo(none.getDescription());
        }
    }
}
