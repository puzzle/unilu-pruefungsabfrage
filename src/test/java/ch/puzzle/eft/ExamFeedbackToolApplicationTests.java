package ch.puzzle.eft;

import ch.puzzle.eft.controller.ExamController;
import ch.puzzle.eft.controller.SiteController;
import ch.puzzle.eft.service.ExamService;
import ch.puzzle.eft.service.ValidationService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ExamFeedbackToolApplicationTests {

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
        assertThat(context.getId()).isEqualTo("exam-feedback-tool");
        checkControllers(context);
        checkServices(context);
    }

    void checkControllers(ApplicationContext context) {
        assertThat(context.getBean(SiteController.class)).isNotNull();
        assertThat(context.getBean(ExamController.class)).isNotNull();
    }

    void checkServices(ApplicationContext context) {
        assertThat(context.getBean(ValidationService.class)).isNotNull();
        assertThat(context.getBean(ExamService.class)).isNotNull();
    }
}
