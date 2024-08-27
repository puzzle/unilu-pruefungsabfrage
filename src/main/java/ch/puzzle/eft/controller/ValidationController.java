package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import ch.puzzle.eft.service.ValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ValidationController {

    @PostMapping("/validate")
    public String viewValidatePage(@Valid ExamNumberForm examNumberForm, BindingResult bindingResult) {
        System.out
                .println(examNumberForm);
        if (bindingResult
                .hasErrors()) {
            return "search";
        }
        return "redirect:result";
    }
}
