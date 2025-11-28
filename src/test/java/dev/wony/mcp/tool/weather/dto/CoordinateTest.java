package dev.wony.mcp.tool.weather.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Coordinate 값 객체 테스트")
class CoordinateTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 위도/경도로 Coordinate를 생성할 수 있다")
        void shouldCreateCoordinate_whenValidLatLon() {
            // given
            double latitude = 37.5665;
            double longitude = 126.9780;

            // when
            Coordinate coordinate = new Coordinate(latitude, longitude);

            // then
            assertThat(coordinate.latitude()).isEqualTo(latitude);
            assertThat(coordinate.longitude()).isEqualTo(longitude);
        }

        @Test
        @DisplayName("위도가 범위를 벗어나면 예외가 발생한다")
        void shouldThrowException_whenLatitudeOutOfRange() {
            // given
            double invalidLatitude = 91.0;
            double validLongitude = 126.9780;

            // when & then
            assertThatThrownBy(() -> new Coordinate(invalidLatitude, validLongitude))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("위도는");
        }

        @Test
        @DisplayName("경도가 범위를 벗어나면 예외가 발생한다")
        void shouldThrowException_whenLongitudeOutOfRange() {
            // given
            double validLatitude = 37.5665;
            double invalidLongitude = 181.0;

            // when & then
            assertThatThrownBy(() -> new Coordinate(validLatitude, invalidLongitude))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("경도는");
        }
    }

    @Nested
    @DisplayName("한국 영역 확인 테스트")
    class IsInKoreaTest {

        @Test
        @DisplayName("서울 좌표는 한국 영역 내에 있다")
        void shouldReturnTrue_whenSeoulCoordinate() {
            // given
            Coordinate seoul = new Coordinate(37.5665, 126.9780);

            // when
            boolean result = seoul.isInKorea();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("부산 좌표는 한국 영역 내에 있다")
        void shouldReturnTrue_whenBusanCoordinate() {
            // given
            Coordinate busan = new Coordinate(35.1796, 129.0756);

            // when
            boolean result = busan.isInKorea();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("제주 좌표는 한국 영역 내에 있다")
        void shouldReturnTrue_whenJejuCoordinate() {
            // given
            Coordinate jeju = new Coordinate(33.4996, 126.5312);

            // when
            boolean result = jeju.isInKorea();

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("도쿄 좌표는 한국 영역 밖에 있다")
        void shouldReturnFalse_whenTokyoCoordinate() {
            // given
            Coordinate tokyo = new Coordinate(35.6762, 139.6503);

            // when
            boolean result = tokyo.isInKorea();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("격자 좌표 변환 테스트")
    class ToGridCoordinateTest {

        @Test
        @DisplayName("Coordinate를 GridCoordinate로 변환할 수 있다")
        void shouldConvertToGridCoordinate() {
            // given
            Coordinate coordinate = new Coordinate(37.5665, 126.9780);

            // when
            GridCoordinate gridCoordinate = coordinate.toGridCoordinate();

            // then
            assertThat(gridCoordinate).isNotNull();
            assertThat(gridCoordinate.nx()).isPositive();
            assertThat(gridCoordinate.ny()).isPositive();
        }
    }

    @Nested
    @DisplayName("포맷팅 테스트")
    class FormattingTest {

        @Test
        @DisplayName("포맷팅된 문자열을 반환한다")
        void shouldReturnFormattedString() {
            // given
            Coordinate coordinate = new Coordinate(37.5665, 126.9780);

            // when
            String formatted = coordinate.toFormattedString();

            // then
            assertThat(formatted).contains("위도");
            assertThat(formatted).contains("경도");
            assertThat(formatted).contains("37.5665");
            assertThat(formatted).contains("126.9780");
        }
    }
}
