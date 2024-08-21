package ch.puzzle.eft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {
    @GetMapping("/")
    public String viewIndexPage(Model model) {
        return "index";
    }
}
