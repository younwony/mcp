package dev.wony.mcp.tool.dto;

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
     */
    public static String interpretSkyCode(String value) {
        return switch (value) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> value;
        };
    }

    /**
     * 강수형태 코드 해석
     */
    public static String interpretPtyCode(String value) {
        return switch (value) {
            case "0" -> "없음";
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "4" -> "소나기";
            case "5" -> "빗방울";
            case "6" -> "빗방울눈날림";
            case "7" -> "눈날림";
            default -> value;
        };
    }

    /**
     * 풍향 값을 16방위로 변환
     */
    public static String interpretWindDirection(String value) {
        try {
            double degree = Double.parseDouble(value);
            int index = (int) ((degree + 22.5 * 0.5) / 22.5);
            String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                    "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};
            return directions[index % 16];
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
