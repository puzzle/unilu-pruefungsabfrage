package ch.puzzle.exam_feedback_tool.controller;

import ch.puzzle.exam_feedback_tool.service.DemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/greeting")
    public ResponseEntity<String> greetPerson(@RequestParam String name) {
        String greeting = demoService
                .greetByName(name);
        return ResponseEntity
                .ok(greeting);
    }

    @GetMapping("/multiply")
    public ResponseEntity<Integer> multiply() {
        int result = demoService
                .multiply(2, 2);
        return ResponseEntity
                .ok(result);
    }
}