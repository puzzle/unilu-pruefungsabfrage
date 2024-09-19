package ch.puzzle.eft.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFound(Model model, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Not Found");
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalServerError(HttpServletRequest req, Exception ex, Model model, RedirectAttributes redirectAttributes) {
        logger.error("Request URL: " + req.getRequestURL() + " raised an " + ex.getClass()
                                                                               .getSimpleName() + " because " + ex.getMessage() + " at " + ex.getStackTrace()[0]);
        redirectAttributes.addFlashAttribute("error", "Internal Server Error");
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/error";
    }

}
