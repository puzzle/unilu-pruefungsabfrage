package ch.puzzle.exam_feedback_tool.controller;


import ch.puzzle.exam_feedback_tool.service.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemoController.class)
@WithMockUser

public class DemoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemoService demoService;


    @Test
    void shouldReceiveGreetingBasedOnName() throws Exception {
        when(demoService.greetByName("Hansjakobli")).thenCallRealMethod();
        this.mockMvc.perform(get("/api/v1/greeting").queryParam("name", "Hansjakobli")).andExpect(status().isOk())
                .andExpect(content().string(equalTo("Hello Hansjakobli")));
    }

    @Test
    void shouldReturnGeneratedRandomNumber() throws Exception {
        when(demoService.multiply(2, 2)).thenReturn(4);
        this.mockMvc.perform(get("/api/v1/multiply")).andExpect(status().isOk())
                .andExpect(content().string(equalTo("4")));
    }

}
