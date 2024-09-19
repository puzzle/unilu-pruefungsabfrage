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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Controller
@SessionAttributes("errorModel")
public class ExceptionController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(Model model, HttpSession session) {
        session.setAttribute("errorModel", new ErrorModel("404", "Resource not found"));
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalServerError(Model model, HttpServletRequest req, Exception ex, HttpSession session) {
        logger.error("Request URL: " + req.getRequestURL() + " raised an " + ex.getClass()
                                                                               .getSimpleName());
        session.setAttribute("errorModel", new ErrorModel("500", ex.getMessage()));
        return "redirect:/error";
    }

    @GetMapping("/error")
    public String viewErrorPage(Model model) {
        return "error";
    }

    @GetMapping("/return")
    public String viewCompleteErrorPage(Model model, HttpSession session) {
        session.removeAttribute("errorModel");
        return "redirect:/";
    }
}
