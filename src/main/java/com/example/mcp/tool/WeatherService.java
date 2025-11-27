package com.example.mcp.tool;

import com.example.mcp.tool.dto.GridCoordinate;
import com.example.mcp.tool.dto.WeatherApiResponse;
import com.example.mcp.tool.dto.WeatherCategory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 기상청 단기예보 조회서비스를 사용하는 날씨 서비스
 * API 문서: https://www.data.go.kr/data/15084084/openapi.do
 */
@Service
public class WeatherService {

    private static final String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    // 주요 도시 격자 좌표 (nx, ny)
    private static final Map<String, GridCoordinate> CITY_COORDINATES = Map.of(
            "서울", new GridCoordinate(60, 127),
            "부산", new GridCoordinate(98, 76),
            "대구", new GridCoordinate(89, 91),
            "인천", new GridCoordinate(55, 124),
            "광주", new GridCoordinate(58, 74),
            "대전", new GridCoordinate(67, 100),
            "울산", new GridCoordinate(102, 84),
            "세종", new GridCoordinate(66, 103),
            "제주", new GridCoordinate(52, 38)
    );

    private final RestClient restClient;
    private final String serviceKey;

    public WeatherService(@Value("${weather.api.service-key}") String serviceKey) {
        this.serviceKey = serviceKey;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    /**
     * 초단기실황조회
     * 실황정보를 조회 (매시각 정시에 생성되고 10분마다 최신 정보로 업데이트)
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 실황 정보
     */
    @Tool(description = "Get current weather observation for a specific latitude/longitude in Korea. Returns real-time weather data including temperature, precipitation, wind, and humidity.")
    public String getUltraSrtNcst(
            @ToolParam(description = "Latitude (위도)") double latitude,
            @ToolParam(description = "Longitude (경도)") double longitude
    ) {
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);
        LocalDateTime now = LocalDateTime.now();

        // 기준시각: 현재 시각에서 한 시간 전, 정시
        LocalDateTime baseDateTime = now.minusHours(1).withMinute(0).withSecond(0).withNano(0);
        String baseDate = baseDateTime.format(DATE_FORMATTER);
        String baseTime = baseDateTime.format(TIME_FORMATTER);

        try {
            WeatherApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getUltraSrtNcst")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("numOfRows", 10)
                            .queryParam("pageNo", 1)
                            .queryParam("dataType", "JSON")
                            .queryParam("base_date", baseDate)
                            .queryParam("base_time", baseTime)
                            .queryParam("nx", grid.nx())
                            .queryParam("ny", grid.ny())
                            .build())
                    .retrieve()
                    .body(WeatherApiResponse.class);

            return formatUltraSrtNcstResponse(response, latitude, longitude, baseDate, baseTime);
        } catch (RestClientException e) {
            return String.format("날씨 정보 조회 실패: %s", e.getMessage());
        }
    }

    /**
     * 초단기예보조회
     * 초단기예보정보를 조회 (매시각 30분에 생성, 6시간 예보)
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 초단기예보 정보
     */
    @Tool(description = "Get ultra short-term weather forecast (up to 6 hours) for a specific latitude/longitude in Korea. Returns hourly forecast data.")
    public String getUltraSrtFcst(
            @ToolParam(description = "Latitude (위도)") double latitude,
            @ToolParam(description = "Longitude (경도)") double longitude
    ) {
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);
        LocalDateTime now = LocalDateTime.now();

        // 기준시각: 현재 시각 기준으로 가장 최근 발표 시각 (매시 30분)
        LocalDateTime baseDateTime;
        if (now.getMinute() < 45) {
            baseDateTime = now.minusHours(1).withMinute(30).withSecond(0).withNano(0);
        } else {
            baseDateTime = now.withMinute(30).withSecond(0).withNano(0);
        }

        String baseDate = baseDateTime.format(DATE_FORMATTER);
        String baseTime = baseDateTime.format(TIME_FORMATTER);

        try {
            WeatherApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getUltraSrtFcst")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("numOfRows", 60)
                            .queryParam("pageNo", 1)
                            .queryParam("dataType", "JSON")
                            .queryParam("base_date", baseDate)
                            .queryParam("base_time", baseTime)
                            .queryParam("nx", grid.nx())
                            .queryParam("ny", grid.ny())
                            .build())
                    .retrieve()
                    .body(WeatherApiResponse.class);

            return formatUltraSrtFcstResponse(response, latitude, longitude, baseDate, baseTime);
        } catch (RestClientException e) {
            return String.format("날씨 예보 조회 실패: %s", e.getMessage());
        }
    }

    /**
     * 단기예보조회
     * 단기예보 정보를 조회 (하루 8회 발표, 3일 예보)
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 단기예보 정보
     */
    @Tool(description = "Get short-term weather forecast (up to 3 days) for a specific latitude/longitude in Korea. Returns detailed forecast including temperature, precipitation, wind, and sky conditions.")
    public String getVilageFcst(
            @ToolParam(description = "Latitude (위도)") double latitude,
            @ToolParam(description = "Longitude (경도)") double longitude
    ) {
        GridCoordinate grid = GridCoordinate.fromLatLon(latitude, longitude);
        LocalDateTime now = LocalDateTime.now();

        // 단기예보 발표시각: 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
        LocalDateTime baseDateTime = getShortTermForecastBaseTime(now);
        String baseDate = baseDateTime.format(DATE_FORMATTER);
        String baseTime = baseDateTime.format(TIME_FORMATTER);

        try {
            WeatherApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getVilageFcst")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("numOfRows", 300)
                            .queryParam("pageNo", 1)
                            .queryParam("dataType", "JSON")
                            .queryParam("base_date", baseDate)
                            .queryParam("base_time", baseTime)
                            .queryParam("nx", grid.nx())
                            .queryParam("ny", grid.ny())
                            .build())
                    .retrieve()
                    .body(WeatherApiResponse.class);

            return formatVilageFcstResponse(response, latitude, longitude, baseDate, baseTime);
        } catch (RestClientException e) {
            return String.format("날씨 예보 조회 실패: %s", e.getMessage());
        }
    }

    /**
     * 단기예보 발표 시각 계산
     * 발표시각: 02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00 (API 제공 시간: 각 10분 이후)
     */
    private LocalDateTime getShortTermForecastBaseTime(LocalDateTime now) {
        int hour = now.getHour();
        int minute = now.getMinute();

        int[] baseTimes = {2, 5, 8, 11, 14, 17, 20, 23};
        int baseHour = 23; // 기본값: 전날 23시

        for (int baseTime : baseTimes) {
            if (hour > baseTime || (hour == baseTime && minute >= 10)) {
                baseHour = baseTime;
            }
        }

        if (baseHour == 23 && hour < 2) {
            // 전날 23시
            return now.minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
        } else {
            return now.withHour(baseHour).withMinute(0).withSecond(0).withNano(0);
        }
    }

    /**
     * 초단기실황 응답 포맷팅
     */
    private String formatUltraSrtNcstResponse(WeatherApiResponse response, double latitude, double longitude,
                                              String baseDate, String baseTime) {
        if (response == null || response.response() == null || response.response().body() == null) {
            return "날씨 정보를 조회할 수 없습니다.";
        }

        String resultCode = response.response().header().resultCode();
        if (!"00".equals(resultCode)) {
            return String.format("API 오류: %s - %s",
                    resultCode, response.response().header().resultMsg());
        }

        var items = response.response().body().items();
        if (items == null || items.item() == null || items.item().isEmpty()) {
            return "날씨 정보가 없습니다.";
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("=== 초단기실황 (위도: %.4f, 경도: %.4f) ===\n", latitude, longitude));
        result.append(String.format("발표시각: %s %s\n\n", baseDate, baseTime));

        Map<String, String> weatherData = new LinkedHashMap<>();
        for (var item : items.item()) {
            String category = item.category();
            String value = item.obsrValue();

            if ("PTY".equals(category)) {
                weatherData.put("강수형태", WeatherCategory.interpretPtyCode(value));
            } else if ("VEC".equals(category)) {
                weatherData.put("풍향", WeatherCategory.interpretWindDirection(value));
            } else {
                try {
                    WeatherCategory cat = WeatherCategory.valueOf(category);
                    weatherData.put(cat.getDescription(), value + cat.getUnit());
                } catch (IllegalArgumentException e) {
                    weatherData.put(category, value);
                }
            }
        }

        weatherData.forEach((key, value) -> result.append(String.format("%s: %s\n", key, value)));

        return result.toString();
    }

    /**
     * 초단기예보 응답 포맷팅
     */
    private String formatUltraSrtFcstResponse(WeatherApiResponse response, double latitude, double longitude,
                                              String baseDate, String baseTime) {
        if (response == null || response.response() == null || response.response().body() == null) {
            return "날씨 예보를 조회할 수 없습니다.";
        }

        String resultCode = response.response().header().resultCode();
        if (!"00".equals(resultCode)) {
            return String.format("API 오류: %s - %s",
                    resultCode, response.response().header().resultMsg());
        }

        var items = response.response().body().items();
        if (items == null || items.item() == null || items.item().isEmpty()) {
            return "날씨 예보 정보가 없습니다.";
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("=== 초단기예보 (위도: %.4f, 경도: %.4f) ===\n", latitude, longitude));
        result.append(String.format("발표시각: %s %s\n\n", baseDate, baseTime));

        // 시간대별로 그룹핑
        Map<String, Map<String, String>> timeGrouped = items.item().stream()
                .collect(Collectors.groupingBy(
                        item -> item.fcstDate() + " " + item.fcstTime(),
                        LinkedHashMap::new,
                        Collectors.toMap(
                                WeatherApiResponse.Item::category,
                                WeatherApiResponse.Item::fcstValue,
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        )
                ));

        timeGrouped.forEach((time, data) -> {
            result.append(String.format("[%s]\n", time));
            data.forEach((category, value) -> {
                if ("PTY".equals(category)) {
                    result.append(String.format("  강수형태: %s\n", WeatherCategory.interpretPtyCode(value)));
                } else if ("SKY".equals(category)) {
                    result.append(String.format("  하늘상태: %s\n", WeatherCategory.interpretSkyCode(value)));
                } else if ("VEC".equals(category)) {
                    result.append(String.format("  풍향: %s\n", WeatherCategory.interpretWindDirection(value)));
                } else {
                    try {
                        WeatherCategory cat = WeatherCategory.valueOf(category);
                        result.append(String.format("  %s: %s%s\n", cat.getDescription(), value, cat.getUnit()));
                    } catch (IllegalArgumentException e) {
                        result.append(String.format("  %s: %s\n", category, value));
                    }
                }
            });
            result.append("\n");
        });

        return result.toString();
    }

    /**
     * 단기예보 응답 포맷팅
     */
    private String formatVilageFcstResponse(WeatherApiResponse response, double latitude, double longitude,
                                            String baseDate, String baseTime) {
        if (response == null || response.response() == null || response.response().body() == null) {
            return "날씨 예보를 조회할 수 없습니다.";
        }

        String resultCode = response.response().header().resultCode();
        if (!"00".equals(resultCode)) {
            return String.format("API 오류: %s - %s",
                    resultCode, response.response().header().resultMsg());
        }

        var items = response.response().body().items();
        if (items == null || items.item() == null || items.item().isEmpty()) {
            return "날씨 예보 정보가 없습니다.";
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("=== 단기예보 (위도: %.4f, 경도: %.4f) ===\n", latitude, longitude));
        result.append(String.format("발표시각: %s %s\n\n", baseDate, baseTime));

        // 시간대별로 그룹핑
        Map<String, Map<String, String>> timeGrouped = items.item().stream()
                .collect(Collectors.groupingBy(
                        item -> item.fcstDate() + " " + item.fcstTime(),
                        LinkedHashMap::new,
                        Collectors.toMap(
                                WeatherApiResponse.Item::category,
                                WeatherApiResponse.Item::fcstValue,
                                (v1, v2) -> v1,
                                LinkedHashMap::new
                        )
                ));

        timeGrouped.forEach((time, data) -> {
            result.append(String.format("[%s]\n", time));
            data.forEach((category, value) -> {
                if ("PTY".equals(category)) {
                    result.append(String.format("  강수형태: %s\n", WeatherCategory.interpretPtyCode(value)));
                } else if ("SKY".equals(category)) {
                    result.append(String.format("  하늘상태: %s\n", WeatherCategory.interpretSkyCode(value)));
                } else if ("VEC".equals(category)) {
                    result.append(String.format("  풍향: %s\n", WeatherCategory.interpretWindDirection(value)));
                } else {
                    try {
                        WeatherCategory cat = WeatherCategory.valueOf(category);
                        result.append(String.format("  %s: %s%s\n", cat.getDescription(), value, cat.getUnit()));
                    } catch (IllegalArgumentException e) {
                        result.append(String.format("  %s: %s\n", category, value));
                    }
                }
            });
            result.append("\n");
        });

        return result.toString();
    }

    /**
     * 주요 도시의 현재 날씨 조회 (초단기실황)
     * 도시 이름으로 간편하게 조회
     */
    @Tool(description = "한국 주요 도시의 현재 날씨를 조회합니다. 지원 도시: 서울, 부산, 대구, 인천, 광주, 대전, 울산, 세종, 제주")
    public String getCurrentWeather(
            @ToolParam(description = "도시명 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 세종, 제주 중 하나)") String city) {

        // ✅ 입력된 도시 이름의 양 끝 공백을 제거합니다.
        String trimmedCity = city.trim();

        GridCoordinate coord = CITY_COORDINATES.get(trimmedCity);
        if (coord == null) {
            return "지원하지 않는 도시입니다. 지원 도시: " + String.join(", ", CITY_COORDINATES.keySet());
        }

        LocalDateTime now = LocalDateTime.now();
        // 초단기실황은 매시간 정시 발표, 10분 후 제공
        // 현재 시각이 40분 이전이면 이전 시간 데이터 조회
        if (now.getMinute() < 40) {
            now = now.minusHours(1);
        }

        String baseDate = now.format(DATE_FORMATTER);
        String baseTime = now.format(DateTimeFormatter.ofPattern("HH")) + "00";

        try {
            WeatherApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/getUltraSrtNcst")
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 10)
                            .queryParam("dataType", "JSON")
                            .queryParam("base_date", baseDate)
                            .queryParam("base_time", baseTime)
                            .queryParam("nx", coord.nx())
                            .queryParam("ny", coord.ny())
                            .build())
                    .retrieve()
                    .body(WeatherApiResponse.class);

            if (response == null || response.response() == null || response.response().body() == null) {
                return city + "의 날씨 정보를 가져올 수 없습니다.";
            }

            return formatCityWeatherResponse(city, baseDate, baseTime, response);

        } catch (RestClientException e) {
            return city + "의 날씨 조회 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    /**
     * 지원 도시 목록 조회
     */
    @Tool(description = "날씨 조회가 가능한 한국 주요 도시 목록을 반환합니다")
    public String getSupportedCities() {
        return "날씨 조회 가능한 도시:\n" + String.join(", ", CITY_COORDINATES.keySet());
    }

    /**
     * 도시별 날씨 응답 포맷팅 (사용자 친화적 이모지 포맷)
     */
    private String formatCityWeatherResponse(String city, String baseDate, String baseTime, WeatherApiResponse response) {
        var items = response.response().body().items();
        if (items == null || items.item() == null || items.item().isEmpty()) {
            return city + "의 날씨 데이터가 없습니다.";
        }

        Map<String, String> weatherData = new LinkedHashMap<>();
        for (var item : items.item()) {
            weatherData.put(item.category(), item.obsrValue());
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("📍 %s 현재 날씨 (기준시각: %s %s시)\n\n",
                city,
                baseDate.substring(4, 6) + "월 " + baseDate.substring(6, 8) + "일",
                baseTime.substring(0, 2)));

        // T1H: 기온(℃)
        if (weatherData.containsKey("T1H")) {
            result.append(String.format("🌡️ 기온: %s°C\n", weatherData.get("T1H")));
        }

        // RN1: 1시간 강수량(mm)
        if (weatherData.containsKey("RN1")) {
            String rain = weatherData.get("RN1");
            result.append(String.format("🌧️ 1시간 강수량: %s\n",
                    rain.equals("0") || rain.equals("강수없음") ? "없음" : rain + "mm"));
        }

        // REH: 습도(%)
        if (weatherData.containsKey("REH")) {
            result.append(String.format("💧 습도: %s%%\n", weatherData.get("REH")));
        }

        // WSD: 풍속(m/s)
        if (weatherData.containsKey("WSD")) {
            result.append(String.format("💨 풍속: %sm/s\n", weatherData.get("WSD")));
        }

        // PTY: 강수형태 (0:없음, 1:비, 2:비/눈, 3:눈, 5:빗방울, 6:진눈깨비, 7:눈날림)
        if (weatherData.containsKey("PTY")) {
            String ptyCode = weatherData.get("PTY");
            String pty = switch (ptyCode) {
                case "0" -> "없음";
                case "1" -> "비";
                case "2" -> "비/눈";
                case "3" -> "눈";
                case "5" -> "빗방울";
                case "6" -> "빗방울눈날림";
                case "7" -> "눈날림";
                default -> ptyCode;
            };
            result.append(String.format("☔ 강수형태: %s\n", pty));
        }

        return result.toString();
    }
}
