package com.example.mcp.controller;

import com.example.mcp.model.McpRequest;
import com.example.mcp.model.McpTool;
import com.example.mcp.service.McpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(McpController.class)
class McpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private McpService mcpService;

    @Test
    void testHealthEndpoint_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/mcp/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.message").value("MCP Server is running"));
    }

    @Test
    void testListTools_ShouldReturnToolsList() throws Exception {
        // Given
        List<McpTool> mockTools = createMockTools();
        when(mcpService.listTools()).thenReturn(mockTools);

        // When & Then
        mockMvc.perform(get("/mcp/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("get_weather"))
                .andExpect(jsonPath("$[0].description").value("Get the current weather for a location"))
                .andExpect(jsonPath("$[1].name").value("calculate"))
                .andExpect(jsonPath("$[1].description").value("Perform mathematical calculations"));
    }

    @Test
    void testCallTool_GetWeather_ShouldReturnWeatherInfo() throws Exception {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "get_weather");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("location", "Seoul");
        params.put("arguments", arguments);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("location", "Seoul");
        mockResult.put("weather", "Sunny and warm");

        when(mcpService.callTool(eq("get_weather"), any(Map.class)))
                .thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/mcp/tools/call")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Seoul"))
                .andExpect(jsonPath("$.weather").value("Sunny and warm"));
    }

    @Test
    void testCallTool_Calculate_ShouldReturnCalculationResult() throws Exception {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "calculate");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("expression", "15 * 23 + 47");
        params.put("arguments", arguments);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("expression", "15 * 23 + 47");
        mockResult.put("result", "392");

        when(mcpService.callTool(eq("calculate"), any(Map.class)))
                .thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/mcp/tools/call")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expression").value("15 * 23 + 47"))
                .andExpect(jsonPath("$.result").value("392"));
    }

    @Test
    void testChat_ShouldReturnChatResponse() throws Exception {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("message", "Hello, how are you?");

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("response", "I'm doing well, thank you!");

        when(mcpService.chat(anyString())).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/mcp/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("I'm doing well, thank you!"));
    }

    @Test
    void testHandleMcpRequest_ToolsList_ShouldReturnTools() throws Exception {
        // Given
        McpRequest request = new McpRequest();
        request.setMethod("tools/list");
        request.setParams(new HashMap<>());

        List<McpTool> mockTools = createMockTools();
        when(mcpService.listTools()).thenReturn(mockTools);

        // When & Then
        mockMvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.result", hasSize(2)));
    }

    @Test
    void testHandleMcpRequest_ToolsCall_ShouldCallTool() throws Exception {
        // Given
        McpRequest request = new McpRequest();
        request.setMethod("tools/call");
        Map<String, Object> params = new HashMap<>();
        params.put("name", "get_weather");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("location", "Seoul");
        params.put("arguments", arguments);
        request.setParams(params);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("location", "Seoul");
        mockResult.put("weather", "Sunny");

        when(mcpService.callTool(eq("get_weather"), any(Map.class)))
                .thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.result.location").value("Seoul"))
                .andExpect(jsonPath("$.result.weather").value("Sunny"));
    }

    @Test
    void testHandleMcpRequest_Chat_ShouldReturnChatResponse() throws Exception {
        // Given
        McpRequest request = new McpRequest();
        request.setMethod("chat");
        Map<String, Object> params = new HashMap<>();
        params.put("message", "Hello!");
        request.setParams(params);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("response", "Hi there!");

        when(mcpService.chat(anyString())).thenReturn(mockResult);

        // When & Then
        mockMvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.result.response").value("Hi there!"));
    }

    @Test
    void testHandleMcpRequest_UnknownMethod_ShouldReturnError() throws Exception {
        // Given
        McpRequest request = new McpRequest();
        request.setMethod("unknown/method");
        request.setParams(new HashMap<>());

        // When & Then
        mockMvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jsonrpc").value("2.0"))
                .andExpect(jsonPath("$.result.error", containsString("Unknown method")));
    }

    private List<McpTool> createMockTools() {
        List<McpTool> tools = new ArrayList<>();

        Map<String, Object> weatherSchema = new HashMap<>();
        weatherSchema.put("type", "object");
        Map<String, Object> weatherProperties = new HashMap<>();
        Map<String, Object> locationProp = new HashMap<>();
        locationProp.put("type", "string");
        locationProp.put("description", "The city name");
        weatherProperties.put("location", locationProp);
        weatherSchema.put("properties", weatherProperties);
        weatherSchema.put("required", List.of("location"));

        tools.add(new McpTool(
                "get_weather",
                "Get the current weather for a location",
                weatherSchema
        ));

        Map<String, Object> calculateSchema = new HashMap<>();
        calculateSchema.put("type", "object");
        Map<String, Object> calcProperties = new HashMap<>();
        Map<String, Object> expressionProp = new HashMap<>();
        expressionProp.put("type", "string");
        expressionProp.put("description", "Mathematical expression to evaluate");
        calcProperties.put("expression", expressionProp);
        calculateSchema.put("properties", calcProperties);
        calculateSchema.put("required", List.of("expression"));

        tools.add(new McpTool(
                "calculate",
                "Perform mathematical calculations",
                calculateSchema
        ));

        return tools;
    }
}
