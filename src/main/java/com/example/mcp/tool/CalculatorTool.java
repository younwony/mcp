package com.example.mcp.tool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Function;

/**
 * 수학 계산을 수행하는 MCP 도구
 * JavaScript 엔진을 사용하여 수식을 평가합니다.
 */
@Configuration
public class CalculatorTool {

    public record CalculateRequest(String expression) {}

    public record CalculateResponse(String expression, String result, String error) {}

    @Bean
    @Description("Perform mathematical calculations. Supports basic arithmetic operations (+, -, *, /) and parentheses.")
    public Function<CalculateRequest, CalculateResponse> calculate() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        return request -> {
            String expression = request.expression();

            try {
                // 보안: 안전한 수식만 허용 (숫자, 연산자, 괄호, 공백만)
                if (!expression.matches("[0-9+\\-*/.() ]+")) {
                    return new CalculateResponse(
                            expression,
                            null,
                            "Invalid expression. Only numbers and basic operators (+, -, *, /, parentheses) are allowed."
                    );
                }

                Object result = engine.eval(expression);
                return new CalculateResponse(expression, result.toString(), null);

            } catch (ScriptException e) {
                return new CalculateResponse(
                        expression,
                        null,
                        "Failed to evaluate expression: " + e.getMessage()
                );
            }
        };
    }
}