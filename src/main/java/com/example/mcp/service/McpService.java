package com.example.mcp.service;

import com.example.mcp.model.McpTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class McpService {

    private final ChatClient chatClient;

    public McpService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<McpTool> listTools() {
        List<McpTool> tools = new ArrayList<>();

        Map<String, Object> weatherSchema = new HashMap<>();
        weatherSchema.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> locationProp = new HashMap<>();
        locationProp.put("type", "string");
        locationProp.put("description", "The city name");
        properties.put("location", locationProp);
        weatherSchema.put("properties", properties);
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

    public Object callTool(String toolName, Map<String, Object> arguments) {
        return switch (toolName) {
            case "get_weather" -> getWeather((String) arguments.get("location"));
            case "calculate" -> calculate((String) arguments.get("expression"));
            default -> Map.of("error", "Unknown tool: " + toolName);
        };
    }

    private Object getWeather(String location) {
        String prompt = String.format("What's the current weather like in %s? Provide a brief summary.", location);
        ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        return Map.of(
                "location", location,
                "weather", response.getResult().getOutput().getContent()
        );
    }

    private Object calculate(String expression) {
        String prompt = String.format("Calculate this mathematical expression: %s. Provide only the numerical result.", expression);
        ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        return Map.of(
                "expression", expression,
                "result", response.getResult().getOutput().getContent()
        );
    }

    public Object chat(String message) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        return Map.of("response", response.getResult().getOutput().getContent());
    }
}
