package com.example.mcp.service;

import com.example.mcp.model.McpTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class McpServiceTest {

    private McpService mcpService;
    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;
    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.PromptUserSpec promptSpec;
    private ChatClient.CallResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        chatClientBuilder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class);
        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        promptSpec = mock(ChatClient.PromptUserSpec.class);
        responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);

        mcpService = new McpService(chatClientBuilder);
    }

    @Test
    void testListTools_ShouldReturnTwoTools() {
        // When
        List<McpTool> tools = mcpService.listTools();

        // Then
        assertNotNull(tools);
        assertEquals(2, tools.size());

        // Verify get_weather tool
        McpTool weatherTool = tools.stream()
                .filter(t -> "get_weather".equals(t.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(weatherTool);
        assertEquals("Get the current weather for a location", weatherTool.getDescription());
        assertNotNull(weatherTool.getInputSchema());

        // Verify calculate tool
        McpTool calculateTool = tools.stream()
                .filter(t -> "calculate".equals(t.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(calculateTool);
        assertEquals("Perform mathematical calculations", calculateTool.getDescription());
        assertNotNull(calculateTool.getInputSchema());
    }

    @Test
    void testListTools_ShouldHaveValidSchemas() {
        // When
        List<McpTool> tools = mcpService.listTools();

        // Then
        for (McpTool tool : tools) {
            assertNotNull(tool.getName());
            assertNotNull(tool.getDescription());
            assertNotNull(tool.getInputSchema());

            Map<String, Object> schema = tool.getInputSchema();
            assertEquals("object", schema.get("type"));
            assertNotNull(schema.get("properties"));
            assertNotNull(schema.get("required"));
        }
    }

    @Test
    void testCallTool_WithUnknownTool_ShouldReturnError() {
        // Given
        String unknownToolName = "unknown_tool";
        Map<String, Object> arguments = new HashMap<>();

        // When
        Object result = mcpService.callTool(unknownToolName, arguments);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertTrue(resultMap.containsKey("error"));
        assertTrue(resultMap.get("error").toString().contains("Unknown tool"));
    }

    @Test
    void testCallTool_GetWeather_ShouldReturnWeatherInfo() {
        // Given
        String location = "Seoul";
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("location", location);

        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(promptSpec);
        when(promptSpec.call()).thenReturn(responseSpec);
        when(responseSpec.chatResponse()).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(new org.springframework.ai.chat.messages.AssistantMessage("Sunny and warm"));

        // When
        Object result = mcpService.callTool("get_weather", arguments);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals(location, resultMap.get("location"));
        assertTrue(resultMap.containsKey("weather"));
    }

    @Test
    void testCallTool_Calculate_ShouldReturnCalculationResult() {
        // Given
        String expression = "15 * 23 + 47";
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("expression", expression);

        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(promptSpec);
        when(promptSpec.call()).thenReturn(responseSpec);
        when(responseSpec.chatResponse()).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(new org.springframework.ai.chat.messages.AssistantMessage("392"));

        // When
        Object result = mcpService.callTool("calculate", arguments);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals(expression, resultMap.get("expression"));
        assertTrue(resultMap.containsKey("result"));
    }

    @Test
    void testChat_ShouldReturnResponse() {
        // Given
        String message = "Hello, how are you?";

        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(promptSpec);
        when(promptSpec.call()).thenReturn(responseSpec);
        when(responseSpec.chatResponse()).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(new org.springframework.ai.chat.messages.AssistantMessage("I'm doing well, thank you!"));

        // When
        Object result = mcpService.chat(message);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertTrue(resultMap.containsKey("response"));
        assertNotNull(resultMap.get("response"));
    }
}
