package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {
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
}
