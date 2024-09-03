package ch.puzzle.eft.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DownloadController.class)
@WithMockUser
public class DownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDownloadFileFromEndpoint() throws Exception {
        mockMvc.perform(get("/download").param("filename", "Handels und Gesellschaftsrecht/11000_11112222.pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }
}
