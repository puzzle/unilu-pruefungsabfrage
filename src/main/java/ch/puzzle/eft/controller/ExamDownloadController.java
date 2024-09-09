package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ExamFileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.File;

@RestController
public class ExamDownloadController {
    ExamFileService examFileService;

    public ExamDownloadController(ExamFileService examFileService) {
        this.examFileService = examFileService;
    }


    @GetMapping(value = "/download/{subject}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("subject") String subject, @PathVariable("fileName") String fileName, HttpServletResponse response) {
        response
                .setHeader("Content-Disposition", "attachment; filename=" + subject + ".pdf");
        File examFile = examFileService
                .getFileToDownload(subject, fileName);
        return ResponseEntity
                .ok(new FileSystemResource(examFile));
    }
}

