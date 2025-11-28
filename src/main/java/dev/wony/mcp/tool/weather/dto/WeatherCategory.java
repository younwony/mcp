package dev.wony.mcp.tool.weather.dto;

/**
 * 기상청 예보 요소 카테고리
 */
public enum WeatherCategory {
    // 단기예보
    POP("강수확률", "%"),
    PTY("강수형태", "코드값"),
    PCP("1시간 강수량", "mm"),
    REH("습도", "%"),
    SNO("1시간 신적설", "cm"),
    SKY("하늘상태", "코드값"),
    TMP("1시간 기온", "℃"),
    TMN("일 최저기온", "℃"),
    TMX("일 최고기온", "℃"),
    UUU("풍속(동서성분)", "m/s"),
    VVV("풍속(남북성분)", "m/s"),
    WAV("파고", "M"),
    VEC("풍향", "deg"),
    WSD("풍속", "m/s"),

    // 초단기실황
    T1H("기온", "℃"),
    RN1("1시간 강수량", "mm"),

    // 초단기예보
    LGT("낙뢰", "kA");

    private final String description;
    private final String unit;

    WeatherCategory(String description, String unit) {
        this.description = description;
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    /**
     * 하늘상태 코드 해석
     *
     * @param value 하늘 상태 코드
     * @return 해석된 하늘 상태 문자열
     * @deprecated {@link dev.wony.mcp.tool.weather.util.WeatherCodeInterpreter#interpretSkyCode(String)} 사용 권장
     */
    @Deprecated(since = "리팩토링", forRemoval = false)
    public static String interpretSkyCode(String value) {
        return dev.wony.mcp.tool.weather.util.WeatherCodeInterpreter.interpretSkyCode(value);
    }

    /**
     * 강수형태 코드 해석
     *
     * @param value 강수 형태 코드
     * @return 해석된 강수 형태 문자열
     * @deprecated {@link dev.wony.mcp.tool.weather.util.WeatherCodeInterpreter#interpretPrecipitationType(String)} 사용 권장
     */
    @Deprecated(since = "리팩토링", forRemoval = false)
    public static String interpretPtyCode(String value) {
        return dev.wony.mcp.tool.weather.util.WeatherCodeInterpreter.interpretPrecipitationType(value);
    }

    /**
     * 풍향 값을 16방위로 변환
     *
     * @param value 풍향 각도 문자열
     * @return 16방위 문자열
     * @deprecated {@link dev.wony.mcp.tool.weather.util.WeatherCodeInterpreter#interpretWindDirection(String)} 사용 권장
     */
    @Deprecated(since = "리팩토링", forRemoval = false)
    public static String interpretWindDirection(String value) {
        return dev.wony.mcp.tool.weather.util.WeatherCodeInterpreter.interpretWindDirection(value);
    }
}
