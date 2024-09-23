package ch.puzzle.eft.controller;


import ch.puzzle.eft.model.ErrorModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Controller
public class ExceptionController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);
    private static final String ERROR_MODEL = "errorModel";

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(Model model, HttpSession session) {
        session.setAttribute(ERROR_MODEL, new ErrorModel("404", "Resource not found"));
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalServerError(Model model, HttpServletRequest req, Exception ex, HttpSession session) {
        logger.warn("Request URL: {} raised an {}",
                    req.getRequestURL(),
                    ex.getClass()
                      .getSimpleName());
        session.setAttribute(ERROR_MODEL, new ErrorModel("unknown", ex.getMessage()));
        return "redirect:/error";
    }

    @GetMapping("/error")
    public String viewErrorPage(Model model) {
        return "error";
    }

    @GetMapping("/return")
    public String viewCompleteErrorPage(Model model, HttpSession session) {
        session.removeAttribute(ERROR_MODEL);
        return "redirect:/";
    }
}
