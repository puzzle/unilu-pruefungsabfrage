package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import ch.puzzle.eft.service.ExamFileService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SiteController {

    ExamFileService examFileService;

    public SiteController(ExamFileService examFileService) {
        this.examFileService = examFileService;
    }

    @GetMapping("/")
    public String viewIndexPage(Model model) {
        return "index";
    }

    @GetMapping("/search")
    public String viewSearchPage(Model model) {
        model
                .addAttribute("examNumberForm", new ExamNumberForm(null));
        return "search";
    }

    @PostMapping("/search")
    public String viewValidatePage(@Valid ExamNumberForm examNumberForm, BindingResult bindingResult, Model model) {
        model.addAttribute("examNumberForm", examNumberForm);
        if (bindingResult.hasErrors()) {
            return "search";
        }
        System.out.println(examFileService.getMatchingExams(examNumberForm.getExamNumber(), "11112222"));
        model.addAttribute("examFiles", examFileService.getMatchingExams(examNumberForm.getExamNumber(), "11112222"));
        System.out.println(model.getAttribute("examFiles"));
        return "search";
    }
}
