package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ExamService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/exams")
public class ExamController {
    ExamService examFileService;

    public ExamController(ExamService examFileService) {
        this.examFileService = examFileService;
    }

    @GetMapping("/download-zip/{examNumber}")
    public ResponseEntity<?> downloadSubject(@PathVariable("examNumber") String examNumber, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=" + examNumber + ".zip");
        examFileService.convertSelectedFilesToZip(examNumber, response.getOutputStream());
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping(value = "/download/{subject}/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("subject") String subject, @PathVariable("fileName") String fileName, HttpServletResponse response) {
        File examFile = examFileService.getFileToDownload(subject, fileName);
        response.setHeader("Content-Disposition", "attachment; filename=" + subject + ".pdf");
        return ResponseEntity.ok(new FileSystemResource(examFile));
    }
}
