package ch.puzzle.eft.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ExamDownloadController {

    @GetMapping("/download/")
    public void downloadExam() {
        // Download exam
    }
}
