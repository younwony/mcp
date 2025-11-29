package dev.wony.mcp.tool.weather.dto;

/**
 * 위도/경도 값 객체 (Value Object)
 *
 * <p>원시값 포장 패턴(Primitive Obsession)을 적용하여 좌표 검증과 비즈니스 로직을 캡슐화합니다.
 * WGS84 좌표계를 사용하며, 생성 시 자동으로 유효성을 검증합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>위도/경도 유효성 검증 (-90~90, -180~180)</li>
 *   <li>한국 영역 내 좌표 여부 확인</li>
 *   <li>기상청 격자 좌표로 변환</li>
 * </ul>
 *
 * @param latitude 위도 (-90.0 ~ 90.0)
 * @param longitude 경도 (-180.0 ~ 180.0)
 *
 * @see GridCoordinate
 * @see dev.wony.mcp.tool.weather.util.CoordinateConverter
 */
public record Coordinate(
        double latitude,
        double longitude
) {
    // 전 세계 좌표 범위
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    // 한국 영역 범위 (대략적인 경계)
    private static final double KOREA_MIN_LATITUDE = 33.0;  // 제주도 남단
    private static final double KOREA_MAX_LATITUDE = 43.0;  // 함경북도 북단
    private static final double KOREA_MIN_LONGITUDE = 124.0; // 서해 최서단
    private static final double KOREA_MAX_LONGITUDE = 132.0; // 동해 최동단

    // 오류 메시지 포맷
    private static final String LATITUDE_ERROR_FORMAT =
            "위도는 %.1f부터 %.1f 사이여야 합니다: %.4f";
    private static final String LONGITUDE_ERROR_FORMAT =
            "경도는 %.1f부터 %.1f 사이여야 합니다: %.4f";

    // 출력 포맷
    private static final String DISPLAY_FORMAT = "위도: %.4f, 경도: %.4f";

    /**
     * Compact Constructor - Record 생성 시 자동으로 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException 위도 또는 경도가 유효하지 않은 경우
     */
    public Coordinate {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    /**
     * 위도 유효성 검증
     *
     * @param latitude 검증할 위도 값
     * @throws IllegalArgumentException 위도가 -90 ~ 90 범위를 벗어난 경우
     */
    private void validateLatitude(double latitude) {
        if (!isValidLatitude(latitude)) {
            throw new IllegalArgumentException(
                    String.format(LATITUDE_ERROR_FORMAT,
                            MIN_LATITUDE, MAX_LATITUDE, latitude)
            );
        }
    }

    /**
     * 경도 유효성 검증
     *
     * @param longitude 검증할 경도 값
     * @throws IllegalArgumentException 경도가 -180 ~ 180 범위를 벗어난 경우
     */
    private void validateLongitude(double longitude) {
        if (!isValidLongitude(longitude)) {
            throw new IllegalArgumentException(
                    String.format(LONGITUDE_ERROR_FORMAT,
                            MIN_LONGITUDE, MAX_LONGITUDE, longitude)
            );
        }
    }

    /**
     * 위도가 유효한 범위 내에 있는지 확인
     *
     * @param latitude 확인할 위도 값
     * @return 유효하면 true, 그렇지 않으면 false
     */
    private static boolean isValidLatitude(double latitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE;
    }

    /**
     * 경도가 유효한 범위 내에 있는지 확인
     *
     * @param longitude 확인할 경도 값
     * @return 유효하면 true, 그렇지 않으면 false
     */
    private static boolean isValidLongitude(double longitude) {
        return longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }

    /**
     * 한국 영역 내의 좌표인지 확인합니다.
     *
     * <p>한국 영역은 대략적인 경계를 사용하며, 제주도부터 휴전선 이북까지 포함합니다.
     *
     * @return 한국 영역 내에 있으면 true, 그렇지 않으면 false
     */
    public boolean isInKorea() {
        return isWithinRange(latitude, KOREA_MIN_LATITUDE, KOREA_MAX_LATITUDE)
                && isWithinRange(longitude, KOREA_MIN_LONGITUDE, KOREA_MAX_LONGITUDE);
    }

    /**
     * 값이 지정된 범위 내에 있는지 확인하는 헬퍼 메서드
     *
     * @param value 확인할 값
     * @param min 최소값 (포함)
     * @param max 최대값 (포함)
     * @return 범위 내에 있으면 true, 그렇지 않으면 false
     */
    private static boolean isWithinRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * 기상청 격자 좌표로 변환합니다.
     *
     * <p>WGS84 경위도 좌표를 기상청 API에서 사용하는 격자 좌표(nx, ny)로 변환합니다.
     * Lambert Conformal Conic Projection 알고리즘을 사용합니다.
     *
     * @return 변환된 격자 좌표
     * @see GridCoordinate
     * @see dev.wony.mcp.tool.weather.util.CoordinateConverter#toGridCoordinate(Coordinate)
     */
    public GridCoordinate toGridCoordinate() {
        return dev.wony.mcp.tool.weather.util.CoordinateConverter.toGridCoordinate(this);
    }

    /**
     * 사람이 읽기 쉬운 형태의 포맷팅된 문자열을 반환합니다.
     *
     * <p>형식: "위도: XX.XXXX, 경도: YY.YYYY"
     *
     * @return 포맷팅된 좌표 문자열
     */
    public String toFormattedString() {
        return String.format(DISPLAY_FORMAT, latitude, longitude);
    }

    /**
     * 두 좌표 간의 거리를 계산합니다 (Haversine 공식 사용).
     *
     * <p>지구를 완전한 구로 가정하여 두 좌표 사이의 대권 거리를 계산합니다.
     *
     * @param other 비교할 다른 좌표
     * @return 두 좌표 사이의 거리 (킬로미터)
     */
    public double distanceTo(Coordinate other) {
        if (other == null) {
            throw new IllegalArgumentException("비교할 좌표는 null일 수 없습니다");
        }

        final double EARTH_RADIUS_KM = 6371.0;
        final double DEGREES_TO_RADIANS = Math.PI / 180.0;

        double lat1Rad = this.latitude * DEGREES_TO_RADIANS;
        double lat2Rad = other.latitude * DEGREES_TO_RADIANS;
        double deltaLat = (other.latitude - this.latitude) * DEGREES_TO_RADIANS;
        double deltaLon = (other.longitude - this.longitude) * DEGREES_TO_RADIANS;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
