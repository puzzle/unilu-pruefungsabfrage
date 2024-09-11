package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ExamFileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class ExamDownloadController {
    ExamFileService examFileService;

    public ExamDownloadController(ExamFileService examFileService) {
        this.examFileService = examFileService;
    }

    @GetMapping("/download-all/{examNumber}")
    public ResponseEntity<?> downloadSubject(@PathVariable("examNumber") String examNumber, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=" + examNumber + ".zip");
        examFileService.convertSelectedFilesToZip(examNumber, response.getOutputStream());
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping(value = "/download/{subject}/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("subject") String subject, @PathVariable("fileName") String fileName, HttpServletResponse response) {
        response.setHeader("Content-Disposition", "attachment; filename=" + subject + ".pdf");
        File examFile = examFileService.getFileToDownload(subject, fileName);
        return ResponseEntity.ok(new FileSystemResource(examFile));
    }
}
