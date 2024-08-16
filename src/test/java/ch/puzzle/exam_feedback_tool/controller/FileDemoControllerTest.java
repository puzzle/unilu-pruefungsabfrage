package ch.puzzle.exam_feedback_tool.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileDemoController.class)
@WithMockUser
public class FileDemoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDownloadFileFromEndpoint() throws Exception {
        File file = new File("static/11111_11112222.pdf");
        this.mockMvc
                .perform(get("/api/v2/download"))
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .bytes(Files
                                .readAllBytes(file
                                        .toPath())));
    }
}
