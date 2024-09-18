package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import ch.puzzle.eft.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SiteController {

    ExamService examFileService;

    public SiteController(ExamService examFileService) {
        this.examFileService = examFileService;
    }

    @GetMapping("/")
    public String viewIndexPage(Model model) {
        return "index";
    }

    @GetMapping("/search")
    public String viewSearchPage(Model model) {
        model.addAttribute("examNumberForm", new ExamNumberForm(null));
        return "search";
    }

    @PostMapping("/search")
    public String viewValidatePage(@Valid ExamNumberForm examNumberForm, BindingResult bindingResult, Model model) {
        model.addAttribute("examNumberForm", examNumberForm);
        if (bindingResult.hasErrors()) {
            return "search";
        }
        try {
            // TODO: Replace hardcoded marticulationNumber 11112222 with dynamic number after login is implemented
            model.addAttribute("examFiles",
                               examFileService.getMatchingExams(examNumberForm.getExamNumber(), "11112222"));
        } catch (ResponseStatusException e) {
            bindingResult.rejectValue("examNumber", "error.examNumberForm", e.getReason());
        }
        return "search";
    }
}
