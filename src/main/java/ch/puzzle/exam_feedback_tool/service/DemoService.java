package ch.puzzle.exam_feedback_tool.service;

import org.springframework.stereotype.Service;

@Service
public class DemoService {
    public String greetByName(String name) {
        return "Hello " + name;
    }

    public int multiply(int a, int b) {
        return a * b;
    }
}