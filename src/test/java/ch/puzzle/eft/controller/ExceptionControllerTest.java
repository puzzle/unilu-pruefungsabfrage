package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ErrorModel;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ExceptionControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ExceptionController exceptionHandlingController;

    @Test
    void shouldCorrectlyHandleInternalServerError() {
        Exception ex = new Exception("Internal error message");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test-url"));

        String viewName = exceptionHandlingController.handleInternalServerError(request, ex, session);

        verify(session, times(1)).setAttribute(eq("errorModel"), any(ErrorModel.class));

        assertEquals("redirect:/error", viewName);
    }

    @Test
    void shouldCorrectlyHandleNotFoundError() {
        String viewName = exceptionHandlingController.handleNotFound(model, session);
        verify(session, times(1)).setAttribute(eq("errorModel"), any(ErrorModel.class));
        assertEquals("redirect:/error", viewName);
    }

    @Test
    void shouldRemoveSessionAttributeWhenReturningToHomePage() {
        String viewName = exceptionHandlingController.viewCompleteErrorPage(model, session);
        verify(session, times(1)).removeAttribute("errorModel");
        assertEquals("redirect:/", viewName);
    }
}
