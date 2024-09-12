package ch.puzzle.eft;

import ch.puzzle.eft.controller.ExamDownloadController;
import ch.puzzle.eft.controller.SiteController;
import ch.puzzle.eft.service.ExamFileService;
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
        assertThat(context.getBean(SiteController.class, ExamDownloadController.class)).isNotNull();
    }

    void checkServices(ApplicationContext context) {
        assertThat(context.getBean(ValidationService.class, ExamFileService.class)).isNotNull();
    }
}
