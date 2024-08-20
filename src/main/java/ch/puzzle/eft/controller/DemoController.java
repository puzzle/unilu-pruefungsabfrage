package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.DemoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/multiplication")
    public String multiply(Model model) {
        int result = demoService
                .multiply(2, 2);
        model
                .addAttribute("result", result);
        return "result";
    }

    @GetMapping("/greeting")
    public String greetPerson(@RequestParam String name, Model model) {
        String greeting = demoService
                .greetByName(name);
        model
                .addAttribute("greeting", greeting);
        return "greeting";
    }
}