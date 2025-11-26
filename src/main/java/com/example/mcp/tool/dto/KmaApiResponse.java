package com.example.mcp.tool.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 기상청 API 응답 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KmaApiResponse(@JsonProperty("response") Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("header") Header header,
            @JsonProperty("body") Body body
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
            @JsonProperty("resultCode") String resultCode,
            @JsonProperty("resultMsg") String resultMsg
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonProperty("dataType") String dataType,
            @JsonProperty("items") Items items,
            @JsonProperty("pageNo") Integer pageNo,
            @JsonProperty("numOfRows") Integer numOfRows,
            @JsonProperty("totalCount") Integer totalCount
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(@JsonProperty("item") List<Item> item) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("baseDate") String baseDate,
            @JsonProperty("baseTime") String baseTime,
            @JsonProperty("category") String category,
            @JsonProperty("fcstDate") String fcstDate,
            @JsonProperty("fcstTime") String fcstTime,
            @JsonProperty("fcstValue") String fcstValue,
            @JsonProperty("nx") Integer nx,
            @JsonProperty("ny") Integer ny
    ) {
    }
}