package ch.puzzle.eft.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExceptionControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private ExceptionController exceptionHandlingController;

    @Test
    void testHandleInternalServerError() {
        Exception ex = new Exception("Internal error message");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test-url"));

        String viewName = exceptionHandlingController.handleInternalServerError(request, ex, model);

        verify(model, times(1)).addAttribute("errorMessage", ex.getMessage());
        verify(model, times(1)).addAttribute("error", "Internal Server Error");

        assertEquals("error", viewName);

    }

    @Test
    void testHandleNotFound() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "http://localhost/test-url");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test-url"));

        String viewName = exceptionHandlingController.handleNotFound(model, ex);

        verify(model, times(1)).addAttribute("error", "Not Found");
        assertEquals("error", viewName);
    }
}
