package ch.puzzle.eft.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionController implements ErrorController {

    Logger logger = LoggerFactory.getLogger(ExceptionController.class.getName());

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(Model model, Exception exception) {
        model.addAttribute("error", "Not Found");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalServerError(HttpServletRequest req, Exception ex, Model model) {
        logger.warn("Request URL: " + req.getRequestURL() + " raised an " + ex.getClass()
                                                                              .getSimpleName() + " because " + ex.getMessage() + " at " + ex.getStackTrace()[0]);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("error", "Internal Server Error");

        return "error";
    }

}
