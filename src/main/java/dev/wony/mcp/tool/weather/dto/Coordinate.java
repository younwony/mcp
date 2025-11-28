package dev.wony.mcp.tool.weather.dto;

/**
 * 위도/경도 값 객체
 * 원시값 포장 패턴을 적용하여 좌표 검증과 비즈니스 로직을 캡슐화
 */
public record Coordinate(
        double latitude,
        double longitude
) {
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    // 한국 영역
    private static final double KOREA_MIN_LATITUDE = 33.0;
    private static final double KOREA_MAX_LATITUDE = 43.0;
    private static final double KOREA_MIN_LONGITUDE = 124.0;
    private static final double KOREA_MAX_LONGITUDE = 132.0;

    public Coordinate {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    private void validateLatitude(double latitude) {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException(
                    String.format("위도는 %.1f부터 %.1f 사이여야 합니다: %.4f",
                            MIN_LATITUDE, MAX_LATITUDE, latitude)
            );
        }
    }

    private void validateLongitude(double longitude) {
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException(
                    String.format("경도는 %.1f부터 %.1f 사이여야 합니다: %.4f",
                            MIN_LONGITUDE, MAX_LONGITUDE, longitude)
            );
        }
    }

    /**
     * 한국 영역 내의 좌표인지 확인
     */
    public boolean isInKorea() {
        return latitude >= KOREA_MIN_LATITUDE && latitude <= KOREA_MAX_LATITUDE
                && longitude >= KOREA_MIN_LONGITUDE && longitude <= KOREA_MAX_LONGITUDE;
    }

    /**
     * 격자 좌표로 변환
     * {@link dev.wony.mcp.tool.weather.util.CoordinateConverter#toGridCoordinate(Coordinate)} 위임
     */
    public GridCoordinate toGridCoordinate() {
        return dev.wony.mcp.tool.weather.util.CoordinateConverter.toGridCoordinate(this);
    }

    /**
     * 포맷팅된 문자열 반환
     */
    public String toFormattedString() {
        return String.format("위도: %.4f, 경도: %.4f", latitude, longitude);
    }
}
