package dev.wony.mcp.tool.weather.dto;

/**
 * 풍향 값 객체
 * 각도 값을 16방위로 변환하는 로직을 캡슐화
 */
public record WindDirection(double degree) {

    private static final double DIRECTION_UNIT = 22.5;
    private static final double DIRECTION_OFFSET = DIRECTION_UNIT * 0.5;
    private static final int DIRECTION_COUNT = 16;
    private static final String[] DIRECTIONS = {
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW",
            "N" // 순환을 위한 마지막 N
    };

    public WindDirection {
        validateDegree(degree);
    }

    private void validateDegree(double degree) {
        if (degree < 0 || degree >= 360) {
            throw new IllegalArgumentException(
                    String.format("풍향은 0도 이상 360도 미만이어야 합니다: %.2f", degree)
            );
        }
    }

    /**
     * 16방위 문자열 반환
     */
    public String toCompassDirection() {
        int index = (int) ((degree + DIRECTION_OFFSET) / DIRECTION_UNIT);
        return DIRECTIONS[index % DIRECTION_COUNT];
    }

    /**
     * 한글 방위 반환
     */
    public String toKoreanDirection() {
        return switch (toCompassDirection()) {
            case "N" -> "북";
            case "NNE" -> "북북동";
            case "NE" -> "북동";
            case "ENE" -> "동북동";
            case "E" -> "동";
            case "ESE" -> "동남동";
            case "SE" -> "남동";
            case "SSE" -> "남남동";
            case "S" -> "남";
            case "SSW" -> "남남서";
            case "SW" -> "남서";
            case "WSW" -> "서남서";
            case "W" -> "서";
            case "WNW" -> "서북서";
            case "NW" -> "북서";
            case "NNW" -> "북북서";
            default -> toCompassDirection();
        };
    }

    /**
     * 문자열에서 WindDirection 생성
     */
    public static WindDirection from(String degreeStr) {
        try {
            double degree = Double.parseDouble(degreeStr);
            return new WindDirection(degree);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("유효하지 않은 풍향 값입니다: %s", degreeStr), e
            );
        }
    }

    @Override
    public String toString() {
        return String.format("%.1f° (%s)", degree, toCompassDirection());
    }
}
