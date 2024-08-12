package ch.puzzle.exam_feedback_tool.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DemoServiceTest {
    @Autowired
    private DemoService demoService;

    @Test
    public void shouldGenerateGreetingWithGivenName() {
        String generatedGreeting = demoService.greetByName("Balthasar");
        assertEquals("Hello Balthasar", generatedGreeting);
    }

}
