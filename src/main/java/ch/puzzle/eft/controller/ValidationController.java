package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ValidationController {

    @PostMapping("/validate")
    public String viewValidatePage(@Valid ExamNumberForm examNumberForm, BindingResult bindingResult) {
        if (bindingResult
                .hasErrors()) {
            return "search";
        }
        return "result";
    }
}
