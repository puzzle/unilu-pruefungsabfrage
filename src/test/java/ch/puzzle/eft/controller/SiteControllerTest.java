package ch.puzzle.eft.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(SiteController.class)
@WithMockUser
public class SiteControllerTest {
    MockMvc mockMvc;

    @Autowired
    public SiteControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

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
}
