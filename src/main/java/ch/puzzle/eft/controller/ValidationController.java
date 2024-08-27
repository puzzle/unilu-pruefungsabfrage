package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ValidationController {
    ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> viewValidatePage(@ModelAttribute("examNumber") String examNumber) {
        System.out.println("examNumber: " + examNumber);
        boolean isValid = validationService.validateExamNumber(examNumber);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(isValid);
    }
}
