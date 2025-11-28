package dev.wony.mcp.tool.weather.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SkyCondition enum 테스트")
class SkyConditionTest {

    @Nested
    @DisplayName("코드로 SkyCondition 찾기")
    class FromCodeTest {

        @ParameterizedTest(name = "코드 {0}은 {1}이다")
        @CsvSource({
                "1, CLEAR",
                "3, PARTLY_CLOUDY",
                "4, CLOUDY"
        })
        @DisplayName("유효한 코드로 SkyCondition을 찾을 수 있다")
        void shouldFindSkyCondition_whenValidCode(String code, SkyCondition expected) {
            // when
            SkyCondition result = SkyCondition.fromCode(code);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("유효하지 않은 코드는 예외가 발생한다")
        void shouldThrowException_whenInvalidCode() {
            // given
            String invalidCode = "99";

            // when & then
            assertThatThrownBy(() -> SkyCondition.fromCode(invalidCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은");
        }
    }

    @Nested
    @DisplayName("코드 해석 테스트")
    class InterpretTest {

        @ParameterizedTest(name = "코드 {0}은 {1}로 해석된다")
        @CsvSource({
                "1, 맑음",
                "3, 구름많음",
                "4, 흐림"
        })
        @DisplayName("코드를 설명 문자열로 변환한다")
        void shouldInterpretCode_correctly(String code, String expected) {
            // when
            String result = SkyCondition.interpret(code);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("유효하지 않은 코드는 원본 코드를 반환한다")
        void shouldReturnOriginalCode_whenInvalidCode() {
            // given
            String invalidCode = "99";

            // when
            String result = SkyCondition.interpret(invalidCode);

            // then
            assertThat(result).isEqualTo(invalidCode);
        }
    }

    @Nested
    @DisplayName("속성 접근 테스트")
    class PropertyTest {

        @Test
        @DisplayName("CLEAR의 속성을 확인한다")
        void shouldHaveCorrectProperties_forClear() {
            // given
            SkyCondition clear = SkyCondition.CLEAR;

            // when & then
            assertThat(clear.getCode()).isEqualTo("1");
            assertThat(clear.getDescription()).isEqualTo("맑음");
            assertThat(clear.getEmoji()).isNotEmpty();
        }

        @Test
        @DisplayName("PARTLY_CLOUDY의 속성을 확인한다")
        void shouldHaveCorrectProperties_forPartlyCloudy() {
            // given
            SkyCondition partlyCloudy = SkyCondition.PARTLY_CLOUDY;

            // when & then
            assertThat(partlyCloudy.getCode()).isEqualTo("3");
            assertThat(partlyCloudy.getDescription()).isEqualTo("구름많음");
            assertThat(partlyCloudy.getEmoji()).isNotEmpty();
        }

        @Test
        @DisplayName("CLOUDY의 속성을 확인한다")
        void shouldHaveCorrectProperties_forCloudy() {
            // given
            SkyCondition cloudy = SkyCondition.CLOUDY;

            // when & then
            assertThat(cloudy.getCode()).isEqualTo("4");
            assertThat(cloudy.getDescription()).isEqualTo("흐림");
            assertThat(cloudy.getEmoji()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("이모지와 설명을 포함한 문자열을 반환한다")
        void shouldReturnFormattedString() {
            // given
            SkyCondition clear = SkyCondition.CLEAR;

            // when
            String result = clear.toString();

            // then
            assertThat(result).contains(clear.getEmoji());
            assertThat(result).contains(clear.getDescription());
        }
    }
}
