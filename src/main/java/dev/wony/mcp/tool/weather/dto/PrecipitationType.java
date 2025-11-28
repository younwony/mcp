package dev.wony.mcp.tool.weather.dto;

/**
 * 강수 형태 값 객체
 */
public enum PrecipitationType {
    NONE("0", "없음", ""),
    RAIN("1", "비", "🌧️"),
    RAIN_SNOW("2", "비/눈", "🌨️"),
    SNOW("3", "눈", "❄️"),
    SHOWER("4", "소나기", "🌦️"),
    DRIZZLE("5", "빗방울", "💧"),
    SLEET("6", "빗방울눈날림", "🌨️"),
    SNOW_DRIFT("7", "눈날림", "🌨️");

    private final String code;
    private final String description;
    private final String emoji;

    PrecipitationType(String code, String description, String emoji) {
        this.code = code;
        this.description = description;
        this.emoji = emoji;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getEmoji() {
        return emoji;
    }

    /**
     * 코드로 PrecipitationType 찾기
     */
    public static PrecipitationType fromCode(String code) {
        for (PrecipitationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 강수 형태 코드입니다: " + code);
    }

    /**
     * 코드를 설명 문자열로 변환
     */
    public static String interpret(String code) {
        try {
            return fromCode(code).description;
        } catch (IllegalArgumentException e) {
            return code;
        }
    }

    /**
     * 강수가 있는지 확인
     */
    public boolean hasPrecipitation() {
        return this != NONE;
    }

    @Override
    public String toString() {
        return emoji.isEmpty() ? description : String.format("%s %s", emoji, description);
    }
}
