package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.AuthorizationService;
import ch.puzzle.eft.service.ExamService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamController.class)
@WithMockUser
class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExamService examFileService;
    @MockBean
    private AuthorizationService authorizationService;


    @Test
    void downloadZipShouldReturnZip() throws Exception {
        String examNumber = "11000";
        when(examFileService.convertSelectedFilesToZip(examNumber)).thenReturn(new ByteArrayOutputStream());
        when(authorizationService.isAuthorized()).thenReturn(true);

        mockMvc.perform(get("/exams/download-zip/{examNumber}", examNumber))
               .andExpect(status().isOk())
               .andExpect(header().string("Content-Disposition", "attachment; filename=11000.zip"));

        verify(examFileService).convertSelectedFilesToZip(examNumber);
    }

    @Test
    void shouldDownloadFileAccordingToSubjectAndFileName() throws Exception {
        File file = new File("static/Privatrecht/11001_22223333.pdf");
        when(examFileService.getFileToDownload("Privatrecht", "11001_22223333.pdf")).thenReturn(file);
        when(authorizationService.isAuthorized()).thenReturn(true);
        this.mockMvc.perform(get("/exams/download/Privatrecht/11001_22223333.pdf"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(Files.readAllBytes(file.toPath())));
    }

    @Test
    void shouldRedirectToErrorPageIfNotAuthorized() throws Exception {
        when(authorizationService.isAuthorized()).thenReturn(false);
        this.mockMvc.perform(get("/exams/download/Privatrecht/11000_11112222.pdf"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/error"));
    }

    @Test
    void shouldReturnErrorPageIfFileNotFound() throws Exception {
        when(authorizationService.isAuthorized()).thenReturn(true);
        when(examFileService.getFileToDownload("Privatrecht", "11000_22223333.pdf")).thenThrow(
                                                                                               new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                                                                           String.format("Keine Unterordner im Pfad %s gefunden",
                                                                                                                                         "Privatrecht")));
        this.mockMvc.perform(get("/exams/download/Privatrecht/11000_22223333.pdf"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/error"))
                    .andExpect(request().sessionAttribute("errorModel", hasProperty("error", is("unknown"))));
    }
}
