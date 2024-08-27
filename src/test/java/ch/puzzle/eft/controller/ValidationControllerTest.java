package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ValidationController.class)
@WithMockUser
public class ValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValidationService validationService;


    @Test
    void shouldNotAcceptNullAsBody() throws Exception {
        when(validationService.validateExamNumber(null)).thenReturn(false);
        this.mockMvc.perform(post("/validate").with(csrf())).andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotAcceptEmptyBody() throws Exception {
        when(validationService.validateExamNumber("")).thenReturn(false);
        this.mockMvc.perform(post("/validate").with(csrf()).contentType(MediaType.APPLICATION_JSON).param("examNumber", "13333")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptValidString() throws Exception {
        when(validationService.validateExamNumber("13333")).thenReturn(true);
        this.mockMvc.perform(post("/validate").with(csrf()).contentType(MediaType.APPLICATION_JSON).param("examNumber", "13333")).andExpect(status().isOk());
    }
}
