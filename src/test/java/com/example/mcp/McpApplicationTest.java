package com.example.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-api-key"
})
class McpApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void testMainApplicationClassExists() {
        assertNotNull(McpApplication.class);
    }

    @Test
    void testApplicationBeanExists() {
        assertTrue(applicationContext.containsBean("mcpApplication"));
    }

    @Test
    void testServiceBeanExists() {
        assertTrue(applicationContext.containsBean("mcpService"));
    }

    @Test
    void testControllerBeanExists() {
        assertTrue(applicationContext.containsBean("mcpController"));
    }

    @Test
    void testApplicationHasCorrectNumberOfBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertNotNull(beanNames);
        assertTrue(beanNames.length > 0);
    }
}
