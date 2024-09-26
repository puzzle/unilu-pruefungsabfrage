package ch.puzzle.eft.controller;

import ch.puzzle.eft.service.ExamService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;

@RestController
@RequestMapping("/exams")
public class ExamController {
    ExamService examFileService;

    public ExamController(ExamService examFileService) {
        this.examFileService = examFileService;
    }

    public boolean isAuthorized() {
        // Todo sobald Login eingeführt wird, hier valide Überprüfung einfügen
        return true;
    }

    @GetMapping("/download-zip/{examNumber}")
    public ResponseEntity<byte[]> downloadSubject(@PathVariable("examNumber") String examNumber) {
        if (!isAuthorized())
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
        if (!isAuthorized())
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
