package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ExamFileService;
import ch.puzzle.eft.test.MockServletOutputStream;
import jakarta.servlet.ServletOutputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamDownloadController.class)
@WithMockUser
class ExamDownloadControllerTest {

    MockServletOutputStream outputStream = new MockServletOutputStream();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExamFileService examFileService;

    @Test
    void downloadZipShouldReturnZip() throws Exception {
        String examNumber = "11000";
        doNothing().when(examFileService)
                   .convertSelectedFilesToZip(examNumber, outputStream);

        mockMvc.perform(get("/download-zip/{examNumber}", examNumber))
               .andExpect(status().isOk())
               .andExpect(header().string("Content-Disposition", "attachment; filename=11000.zip"));

        verify(examFileService).convertSelectedFilesToZip(eq(examNumber), any(ServletOutputStream.class));
    }

    @Test
    void shouldDownloadFileAccordingToSubjectAndFileName() throws Exception {
        File file = new File("static/Privatrecht/11001_22223333.pdf");
        when(examFileService.getFileToDownload("Privatrecht", "11001_22223333.pdf")).thenReturn(file);
        this.mockMvc.perform(get("/download/Privatrecht/11001_22223333.pdf"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(Files.readAllBytes(file.toPath())));
    }

    @Test
    void shouldThrowExceptionIfFileNotFound() throws Exception {
        when(examFileService.getFileToDownload("Privatrecht", "11000_22223333.pdf")).thenThrow(
                                                                                               new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                                                                           String.format("Keine Unterordner im Pfad %s gefunden",
                                                                                                                                         "Privatrecht")));
        ResultActions notFoundResult = this.mockMvc.perform(get("/download/Privatrecht/11000_22223333.pdf"));
        assertEquals(HttpStatus.NOT_FOUND.value(),
                     notFoundResult.andReturn()
                                   .getResponse()
                                   .getStatus());
    }
}
