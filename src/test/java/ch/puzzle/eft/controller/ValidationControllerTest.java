package ch.puzzle.eft.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ValidationController.class)
@WithMockUser
public class ValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldNotAcceptEmptyBody() throws Exception {
        this.mockMvc
                .perform(post("/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", ""))
                .andExpect(status()
                        .isOk())
                .andExpect(model()
                        .hasErrors());

    }

    @Test
    void shouldNotAcceptMoreThan5Digits() throws Exception {

        this.mockMvc
                .perform(post("/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", "1231313"))
                .andExpect(status()
                        .isOk())
                .andExpect(model()
                        .hasErrors());

    }

    @Test
    void shouldAcceptValidString() throws Exception {

        this.mockMvc
                .perform(post("/validate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("examNumber", "20202"))
                .andExpect(status()
                        .isOk())
                .andExpect(model()
                        .hasNoErrors());
    }
}
