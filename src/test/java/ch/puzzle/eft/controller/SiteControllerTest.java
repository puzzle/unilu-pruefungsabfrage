package ch.puzzle.eft.controller;

import java.io.File;
import java.util.List;

import ch.puzzle.eft.model.ExamModel;
import ch.puzzle.eft.service.ExamService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.Cookie;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SiteController.class)
@WithMockUser
class SiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamService examFileService;

    @Test
    void shouldReturnIndexPageWhenAccessingDefaultRoute() throws Exception {
        this.mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
    }

    @Test
    void shouldReturnSearchPageWhenAccessingSearchRoute() throws Exception {
        this.mockMvc.perform(get("/search"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("search"));
    }

    @Test
    void shouldNotAcceptEmptyBody() throws Exception {
        this.mockMvc.perform(post("/search").with(csrf())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .param("examNumber", ""))
                    .andExpect(status().isOk())
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeDoesNotExist("examFiles"));

    }

    @Test
    void shouldNotAcceptMoreThan5Digits() throws Exception {
        this.mockMvc.perform(post("/search").with(csrf())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .param("examNumber", "1231313"))
                    .andExpect(status().isOk())
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeDoesNotExist("examFiles"));

    }

    @Test
    void shouldAcceptValidString() throws Exception {
        when(examFileService.getMatchingExams("11000", "11112222")).thenReturn(List.of(new ExamModel(new File(
                                                                                                              "./Privatrecht/11000_11112222.pdf"))));
        this.mockMvc.perform(post("/search").with(csrf())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .param("examNumber", "11000"))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeExists("examFiles"));
    }

    @Test
    void shouldRespondWithHttpStatus404WhenEnteringExamNumberWithNoMatchingFiles() throws Exception {
        when(examFileService.getMatchingExams("11000", "11112222")).thenThrow(new ResponseStatusException(
                                                                                                          HttpStatus.NOT_FOUND,
                                                                                                          String.format("Keine Prüfungen für die Prüfungslaufnummer %s gefunden",
                                                                                                                        "11000")));

        this.mockMvc.perform(post("/search").with(csrf())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .param("examNumber", "11000"))
                    .andExpect(status().isOk())
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeDoesNotExist("examFiles"));
    }

    @Test
    void shouldCreateCookie() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/").with(csrf()))
                                          .andExpect(status().isFound())
                                          .andExpect(header().string(HttpHeaders.LOCATION, "/"))
                                          .andReturn();
        String cookieHeader = mvcResult.getResponse()
                                       .getHeader(HttpHeaders.SET_COOKIE);
        Assertions.assertNotNull(cookieHeader);
        Assertions.assertTrue(cookieHeader.contains("SameSite=Strict"));
        Assertions.assertTrue(cookieHeader.contains("cookie-consent=true;"));
        Assertions.assertTrue(cookieHeader.contains("Max-Age=31536000;"));
        Assertions.assertTrue(cookieHeader.contains("HttpOnly;"));
        //        Assertions.assertTrue(cookieHeader.contains("Secure;")); TODO: do as soon as login is in main
    }

    @Test
    void shouldRespondWithCookiesMissingFalseWhenCookieConsentGiven() throws Exception {
        mockMvc.perform(get("/").cookie(new Cookie("cookie-consent", "true")))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attribute("cookiesMissing", false));
    }

    @Test
    void shouldRespondWithCookiesMissingTrueWhenCookieConsentNotGiven() throws Exception {
        mockMvc.perform(get("/").cookie(new Cookie("cookie-consent", "false")))
               .andExpect(status().isOk())
               .andExpect(view().name("index"))
               .andExpect(model().attribute("cookiesMissing", true));
    }
}