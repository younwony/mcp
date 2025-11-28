package dev.wony.mcp.tool.weather.dto;

/**
 * 하늘 상태 값 객체
 */
public enum SkyCondition {
    CLEAR("1", "맑음", "☀️"),
    PARTLY_CLOUDY("3", "구름많음", "⛅"),
    CLOUDY("4", "흐림", "☁️");

    private final String code;
    private final String description;
    private final String emoji;

    SkyCondition(String code, String description, String emoji) {
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
     * 코드로 SkyCondition 찾기
     */
    public static SkyCondition fromCode(String code) {
        for (SkyCondition condition : values()) {
            if (condition.code.equals(code)) {
                return condition;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 하늘 상태 코드입니다: " + code);
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

    @Override
    public String toString() {
        return String.format("%s %s", emoji, description);
    }
}
