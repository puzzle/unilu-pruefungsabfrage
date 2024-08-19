package ch.puzzle.eft.controller;


import ch.puzzle.eft.service.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DemoController.class)
@WithMockUser

public class DemoControllerTest {

    MockMvc mockMvc;

    @Autowired
    public DemoControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockBean
    private DemoService demoService;

    @Test
    void shouldReceiveGreetingBasedOnName() throws Exception {
        when(demoService
                .greetByName("Harald"))
                .thenReturn("Hello Harald");
        this.mockMvc
                .perform(get("/greeting")
                        .queryParam("name", "Harald"))
                .andExpect(status()
                        .isOk())
                .andExpect(view()
                        .name("greeting"))
                .andExpect(model()
                        .attribute("greeting", "Hello Harald"));
    }

    @Test
    void shouldReturnGeneratedRandomNumber() throws Exception {
        when(demoService
                .multiply(2, 2))
                .thenReturn(4);
        this.mockMvc
                .perform(get("/"))
                .andExpect(status()
                        .isOk())
                .andExpect(view()
                        .name("result"))
                .andExpect(model()
                        .attribute("result", 4));
    }
}