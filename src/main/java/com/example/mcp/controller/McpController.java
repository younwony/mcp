package com.example.mcp.controller;

import com.example.mcp.model.McpRequest;
import com.example.mcp.model.McpResponse;
import com.example.mcp.model.McpTool;
import com.example.mcp.service.McpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final McpService mcpService;

    public McpController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @PostMapping
    public ResponseEntity<McpResponse> handleMcpRequest(@RequestBody McpRequest request) {
        Object result = switch (request.getMethod()) {
            case "tools/list" -> listTools();
            case "tools/call" -> callTool(request.getParams());
            case "chat" -> chat(request.getParams());
            default -> Map.of("error", "Unknown method: " + request.getMethod());
        };

        return ResponseEntity.ok(new McpResponse(result));
    }

    @GetMapping("/tools")
    public ResponseEntity<List<McpTool>> listTools() {
        return ResponseEntity.ok(mcpService.listTools());
    }

    @PostMapping("/tools/call")
    public ResponseEntity<Object> callTool(@RequestBody Map<String, Object> params) {
        String toolName = (String) params.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

        Object result = mcpService.callTool(toolName, arguments);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chat")
    public ResponseEntity<Object> chat(@RequestBody Map<String, Object> params) {
        String message = (String) params.get("message");
        Object result = mcpService.chat(message);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "MCP Server is running");
        return ResponseEntity.ok(response);
    }
}
