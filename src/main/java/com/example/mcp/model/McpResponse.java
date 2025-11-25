package com.example.mcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpResponse {
    private String jsonrpc = "2.0";
    private Object result;
    private String id;

    public McpResponse(Object result) {
        this.result = result;
    }
}
