package ch.puzzle.eft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExamDownloadController {

    @GetMapping("/download/")
    public void downloadExam() {
        // Download exam
    }
}
