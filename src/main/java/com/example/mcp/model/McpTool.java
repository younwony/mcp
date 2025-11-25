package com.example.mcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpTool {
    private String name;
    private String description;
    private Map<String, Object> inputSchema;
}
