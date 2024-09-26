package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.AuthorizationService;
import ch.puzzle.eft.service.ExamService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;

@RestController
@RequestMapping("/exams")
public class ExamController {

    ExamService examFileService;

    AuthorizationService authorizationService;

    public ExamController(ExamService examFileService, AuthorizationService authorizationService) {
        this.examFileService = examFileService;
        this.authorizationService = authorizationService;
    }


    @GetMapping("/download-zip/{examNumber}")
    public ResponseEntity<byte[]> downloadSubject(@PathVariable("examNumber") String examNumber) {
        if (!authorizationService.isAuthorized())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                              "Du bist nicht berechtigt, diese Datei herunterzuladen.");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition", "attachment; filename=" + examNumber + ".zip");
        ByteArrayOutputStream byteArrayOutputStream = examFileService.convertSelectedFilesToZip(examNumber);
        return ResponseEntity.ok()
                             .headers(responseHeaders)
                             .body(byteArrayOutputStream.toByteArray());
    }

    @GetMapping(value = "/download/{subject}/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("subject") String subject, @PathVariable("fileName") String fileName) {
        if (!authorizationService.isAuthorized())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                              "Du bist nicht berechtigt, diese Datei herunterzuladen.");
        File examFile = examFileService.getFileToDownload(subject, fileName);
        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.set("Content-Disposition", "attachment; filename=" + subject + ".pdf");
        return ResponseEntity.ok()
                             .headers(responseHeaders)
                             .body(new FileSystemResource(examFile));
    }
}
