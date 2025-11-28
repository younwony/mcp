package dev.wony.mcp.tool.weather.dto;

import dev.wony.mcp.tool.weather.util.CoordinateConverter;

/**
 * 기상청 격자 좌표
 *
 * <p>Note: 좌표 변환 로직은 {@link CoordinateConverter}로 이동
 */
public record GridCoordinate(int nx, int ny) {

    /**
     * 위경도를 격자 좌표로 변환
     * Lambert Conformal Conic Projection 사용
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 격자 좌표
     * @deprecated {@link CoordinateConverter#toGridCoordinate(double, double)} 사용 권장
     */
    @Deprecated(since = "리팩토링", forRemoval = false)
    public static GridCoordinate fromLatLon(double latitude, double longitude) {
        return CoordinateConverter.toGridCoordinate(latitude, longitude);
    }
}
