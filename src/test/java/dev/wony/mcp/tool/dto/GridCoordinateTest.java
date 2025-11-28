package dev.wony.mcp.tool.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GridCoordinate 테스트")
class GridCoordinateTest {

    @Test
    @DisplayName("서울(위도 37.5665, 경도 126.9780)의 격자 좌표는 (60, 127)이다")
    void fromLatLon_Seoul() {
        // given
        double latitude = 37.5665;
        double longitude = 126.9780;

        // when
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);

        // then
        assertThat(grid.nx()).isEqualTo(60);
        assertThat(grid.ny()).isEqualTo(127);
    }

    @Test
    @DisplayName("부산(위도 35.1796, 경도 129.0756)의 격자 좌표는 (98, 76)이다")
    void fromLatLon_Busan() {
        // given
        double latitude = 35.1796;
        double longitude = 129.0756;

        // when
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);

        // then
        assertThat(grid.nx()).isEqualTo(98);
        assertThat(grid.ny()).isEqualTo(76);
    }

    @Test
    @DisplayName("제주시(위도 33.4996, 경도 126.5312)의 격자 좌표는 (53, 38)이다")
    void fromLatLon_Jeju() {
        // given
        double latitude = 33.4996;
        double longitude = 126.5312;

        // when
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);

        // then
        assertThat(grid.nx()).isEqualTo(53);
        assertThat(grid.ny()).isEqualTo(38);
    }

    @Test
    @DisplayName("대전(위도 36.3504, 경도 127.3845)의 격자 좌표는 (67, 100)이다")
    void fromLatLon_Daejeon() {
        // given
        double latitude = 36.3504;
        double longitude = 127.3845;

        // when
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);

        // then
        assertThat(grid.nx()).isEqualTo(67);
        assertThat(grid.ny()).isEqualTo(100);
    }
}
