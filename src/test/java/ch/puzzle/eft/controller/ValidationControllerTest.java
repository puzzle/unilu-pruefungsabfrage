package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ValidationController.class)
@WithMockUser
public class ValidationControllerTest {

    private final ObjectMapper oM = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldNotAcceptNullAsBody() throws Exception {

        this.mockMvc
                .perform(post("/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(oM
                                .writeValueAsString(new ExamNumberForm(null))))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl("search"));
    }

    @Test
    void shouldNotAcceptEmptyBody() throws Exception {
        this.mockMvc
                .perform(post("/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(oM
                                .writeValueAsString(new ExamNumberForm(""))))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl("search"));
    }

    @Test
    void shouldAcceptValidString() throws Exception {
        this.mockMvc
                .perform(post("/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(oM
                                .writeValueAsString(new ExamNumberForm("20202"))))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl("result"));
    }
}
