package ch.puzzle.eft;

import ch.puzzle.eft.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ExamFeedbackToolApplicationTest {

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
        assertThat(context.getId()).isEqualTo("exam-feedback-tool");
        checkControllers(context);
    }

    void checkControllers(ApplicationContext context) {
        assertThat(context.getBean(HomeController.class)).isNotNull();
    }
}
