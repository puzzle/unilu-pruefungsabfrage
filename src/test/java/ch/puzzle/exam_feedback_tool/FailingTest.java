package ch.puzzle.exam_feedback_tool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FailingTest {

    @Test
    void Test() {
        int number = 1;
        assertEquals(2, number);
    }
}
