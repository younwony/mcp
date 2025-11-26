package com.example.mcp.tool.dto;

/**
 * 날씨 정보를 사람이 읽기 쉬운 형태로 변환한 DTO
 */
public record WeatherInfo(
        String date,
        String time,
        String temperature,
        String precipitation,
        String humidity,
        String skyCondition,
        String windSpeed
) {

    /**
     * 하늘 상태 코드를 텍스트로 변환
     * @param code 하늘 상태 코드 (1: 맑음, 3: 구름많음, 4: 흐림)
     * @return 하늘 상태 텍스트
     */
    public static String getSkyConditionText(String code) {
        return switch (code) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "알 수 없음";
        };
    }

    /**
     * 강수 형태 코드를 텍스트로 변환
     * @param code 강수 형태 코드 (0: 없음, 1: 비, 2: 비/눈, 3: 눈, 4: 소나기)
     * @return 강수 형태 텍스트
     */
    public static String getPrecipitationTypeText(String code) {
        return switch (code) {
            case "0" -> "없음";
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "4" -> "소나기";
            default -> "알 수 없음";
        };
    }
}
