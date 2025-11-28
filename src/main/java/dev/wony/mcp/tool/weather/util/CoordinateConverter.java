package dev.wony.mcp.tool.weather.util;

import dev.wony.mcp.tool.weather.dto.Coordinate;
import dev.wony.mcp.tool.weather.dto.GridCoordinate;

/**
 * 좌표 변환 유틸리티 클래스
 * WGS84 경위도 좌표를 기상청 격자 좌표로 변환
 * Lambert Conformal Conic Projection 사용
 */
public final class CoordinateConverter {

    // 기상청 격자 정보 상수
    private static final double EARTH_RADIUS_KM = 6371.00877;
    private static final double GRID_SIZE_KM = 5.0;
    private static final double STANDARD_LATITUDE_1 = 30.0;
    private static final double STANDARD_LATITUDE_2 = 60.0;
    private static final double REFERENCE_LONGITUDE = 126.0;
    private static final double REFERENCE_LATITUDE = 38.0;
    private static final double REFERENCE_GRID_X = 210.0 / GRID_SIZE_KM;
    private static final double REFERENCE_GRID_Y = 675.0 / GRID_SIZE_KM;

    private static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

    private CoordinateConverter() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * 위경도를 격자 좌표로 변환
     *
     * @param coordinate 위경도 좌표
     * @return 격자 좌표
     */
    public static GridCoordinate toGridCoordinate(Coordinate coordinate) {
        return toGridCoordinate(coordinate.latitude(), coordinate.longitude());
    }

    /**
     * 위경도를 격자 좌표로 변환
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 격자 좌표
     */
    public static GridCoordinate toGridCoordinate(double latitude, double longitude) {
        LambertProjection projection = calculateLambertProjection();

        double latitudeRad = latitude * DEGREES_TO_RADIANS;
        double longitudeRad = longitude * DEGREES_TO_RADIANS;

        ProjectionResult result = projectToGrid(
                latitudeRad,
                longitudeRad,
                projection
        );

        int nx = (int) (result.x() + REFERENCE_GRID_X + 1.5);
        int ny = (int) (result.y() + REFERENCE_GRID_Y + 1.5);

        return new GridCoordinate(nx, ny);
    }

    /**
     * Lambert Conformal Conic Projection 파라미터 계산
     */
    private static LambertProjection calculateLambertProjection() {
        double re = EARTH_RADIUS_KM / GRID_SIZE_KM;

        double slat1 = STANDARD_LATITUDE_1 * DEGREES_TO_RADIANS;
        double slat2 = STANDARD_LATITUDE_2 * DEGREES_TO_RADIANS;
        double olon = REFERENCE_LONGITUDE * DEGREES_TO_RADIANS;
        double olat = REFERENCE_LATITUDE * DEGREES_TO_RADIANS;

        // sn 계산
        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        // sf 계산
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        // ro 계산
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        return new LambertProjection(re, sn, sf, ro, olon);
    }

    /**
     * 좌표를 격자로 투영
     */
    private static ProjectionResult projectToGrid(
            double latitudeRad,
            double longitudeRad,
            LambertProjection projection
    ) {
        // ra 계산
        double ra = Math.tan(Math.PI * 0.25 + latitudeRad * 0.5);
        ra = projection.re() * projection.sf() / Math.pow(ra, projection.sn());

        // theta 계산
        double theta = longitudeRad - projection.olon();
        if (theta > Math.PI) {
            theta -= 2.0 * Math.PI;
        }
        if (theta < -Math.PI) {
            theta += 2.0 * Math.PI;
        }
        theta *= projection.sn();

        // 격자 좌표 계산
        double x = ra * Math.sin(theta);
        double y = projection.ro() - ra * Math.cos(theta);

        return new ProjectionResult(x, y);
    }

    /**
     * Lambert Projection 파라미터
     */
    private record LambertProjection(
            double re,    // 반경
            double sn,    // 투영 상수
            double sf,    // 축척 계수
            double ro,    // 기준점 반경
            double olon   // 기준 경도(라디안)
    ) {
    }

    /**
     * 투영 결과
     */
    private record ProjectionResult(double x, double y) {
    }
}
