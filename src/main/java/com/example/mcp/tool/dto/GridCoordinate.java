package com.example.mcp.tool.dto;

/**
 * 기상청 격자 좌표
 */
public record GridCoordinate(
        int nx,
        int ny
) {
    /**
     * 위경도를 격자 좌표로 변환
     * Lambert Conformal Conic Projection 사용
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 격자 좌표
     */
    public static GridCoordinate fromLatLon(double latitude, double longitude) {
        // 기상청 격자 정보
        final double RE = 6371.00877; // 지구 반경 (km)
        final double GRID = 5.0; // 격자 간격 (km)
        final double SLAT1 = 30.0; // 표준위도 1
        final double SLAT2 = 60.0; // 표준위도 2
        final double OLON = 126.0; // 기준점 경도
        final double OLAT = 38.0; // 기준점 위도
        final double XO = 210.0 / GRID; // 기준점 X좌표
        final double YO = 675.0 / GRID; // 기준점 Y좌표

        final double DEGRAD = Math.PI / 180.0;
        final double re = RE / GRID;
        final double slat1 = SLAT1 * DEGRAD;
        final double slat2 = SLAT2 * DEGRAD;
        final double olon = OLON * DEGRAD;
        final double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + latitude * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = longitude * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int nx = (int) (ra * Math.sin(theta) + XO + 1.5);
        int ny = (int) (ro - ra * Math.cos(theta) + YO + 1.5);

        return new GridCoordinate(nx, ny);
    }
}
