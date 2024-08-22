package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ValidationController {

    ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/validate")
    public String viewValidatePage(@RequestParam("examNumber") String examNumber, Model model) {
        model.addAttribute("examNumberValidation", validationService.validateExamNumber(examNumber));
        return "index";
    }
}
