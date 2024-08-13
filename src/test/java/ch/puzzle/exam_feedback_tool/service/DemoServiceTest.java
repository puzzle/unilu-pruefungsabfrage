package ch.puzzle.exam_feedback_tool.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DemoServiceTest {

    DemoService demoService;

    @Autowired
    public DemoServiceTest(DemoService demoService) {
        this.demoService = demoService;
    }

    @Test
    public void shouldGenerateGreetingWithGivenName() {
        String generatedGreeting = demoService
                .greetByName("Balthasar");
        assertEquals("Hello Balthasar", generatedGreeting);
    }

    @Test
    public void shouldReturnCalculatedNumber() {
        int result = demoService
                .multiply(2, 2);
        assertEquals(2 * 2, result);
    }
}