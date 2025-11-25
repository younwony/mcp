package com.example.mcp.tool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Map;
import java.util.function.Function;

/**
 * 날씨 정보를 제공하는 MCP 도구
 * 실제로는 외부 날씨 API를 호출해야 하지만, 여기서는 모의 데이터를 반환합니다.
 */
@Configuration
public class WeatherTool {

    public record WeatherRequest(String location) {}

    public record WeatherResponse(
            String location,
            String temperature,
            String condition,
            String humidity,
            String description
    ) {}

    @Bean
    @Description("Get the current weather for a location")
    public Function<WeatherRequest, WeatherResponse> getWeather() {
        return request -> {
            String location = request.location();
            String weatherDescription = generateMockWeather(location);

            return new WeatherResponse(
                    location,
                    "15°C",
                    "Partly Cloudy",
                    "65%",
                    weatherDescription
            );
        };
    }

    private String generateMockWeather(String location) {
        return String.format(
                "%s is currently experiencing partly cloudy conditions with a temperature of around 15°C. " +
                "Humidity is at 65%% and there's a light breeze from the west.",
                location
        );
    }
}