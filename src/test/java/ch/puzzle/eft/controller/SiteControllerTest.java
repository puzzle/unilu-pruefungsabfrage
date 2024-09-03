package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamFileModel;
import ch.puzzle.eft.service.ExamFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SiteController.class)
@WithMockUser
class SiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamFileService examFileService;

    @Test
    void defaultRouteShouldReturnIndexPage() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(status()
                        .isOk())
                .andExpect(view()
                        .name("index"));
    }

    @Test
    void searchRouteShouldReturnSearchPage() throws Exception {
        this.mockMvc
                .perform(get("/search"))
                .andExpect(status()
                        .isOk())
                .andExpect(view()
                        .name("search"));
    }

    @Test
    void shouldNotAcceptEmptyBody() throws Exception {
        this.mockMvc
                .perform(post("/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", ""))
                .andExpect(status()
                        .isOk())
                .andExpect(model()
                        .hasErrors()).andExpect(model().attributeDoesNotExist("examFiles"));

    }

    @Test
    void shouldNotAcceptMoreThan5Digits() throws Exception {
        this.mockMvc
                .perform(post("/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", "1231313"))
                .andExpect(status()
                        .isOk())
                .andExpect(model()
                        .hasErrors()).andExpect(model().attributeDoesNotExist("examFiles"));

    }

    @Test
    void shouldAcceptValidString() throws Exception {
        when(examFileService
                .getMatchingExams("11000", "11112222"))
                .thenReturn(List
                        .of(new ExamFileModel(new File("./Privatrecht/11000_11112222.pdf"))));
        this.mockMvc
                .perform(post("/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", "11000"))
                .andExpect(status()
                        .isOk())
                .andExpect(model()
                        .hasNoErrors())
                .andExpect(model()
                        .attributeExists("examFiles"));
    }

    @Test
    void shouldNotAcceptExamNumberWithNoMatchingFiles() throws Exception {
        when(examFileService.getMatchingExams("11000", "11112222"))
                .thenReturn(List.of());

        this.mockMvc.perform(post("/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", "11000"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeDoesNotExist("examFiles"));
    }

}
