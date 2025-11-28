package dev.wony.mcp.tool.weather.util;

import dev.wony.mcp.tool.weather.dto.PrecipitationType;
import dev.wony.mcp.tool.weather.dto.SkyCondition;
import dev.wony.mcp.tool.weather.dto.WindDirection;

/**
 * 날씨 코드 해석 유틸리티 클래스
 * 기상청 API의 코드 값을 사람이 읽기 쉬운 형태로 변환
 *
 * <p>Note: 이 클래스는 {@link dev.wony.mcp.tool.dto.WeatherCategory}의 deprecated 메서드들에 의해 사용됩니다.
 */
public final class WeatherCodeInterpreter {

    private WeatherCodeInterpreter() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * 하늘 상태 코드 해석
     *
     * @param code 하늘 상태 코드 (1: 맑음, 3: 구름많음, 4: 흐림)
     * @return 해석된 하늘 상태 문자열
     */
    public static String interpretSkyCode(String code) {
        return SkyCondition.interpret(code);
    }

    /**
     * 강수 형태 코드 해석
     *
     * @param code 강수 형태 코드 (0: 없음, 1: 비, 2: 비/눈, 3: 눈, etc.)
     * @return 해석된 강수 형태 문자열
     */
    public static String interpretPrecipitationType(String code) {
        return PrecipitationType.interpret(code);
    }

    /**
     * 풍향 값을 16방위로 변환
     *
     * @param degreeStr 풍향 각도 문자열
     * @return 16방위 문자열 (N, NNE, NE, etc.)
     */
    public static String interpretWindDirection(String degreeStr) {
        try {
            WindDirection windDirection = WindDirection.from(degreeStr);
            return windDirection.toCompassDirection();
        } catch (IllegalArgumentException e) {
            return degreeStr;
        }
    }

}
