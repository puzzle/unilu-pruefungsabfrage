package ch.puzzle.eft.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/v2")
public class FileDemoController {

    private Environment environment;

    public FileDemoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(HttpServletResponse response) {
        String basePath = environment
                .getProperty("RESOURCE_DIR", "");
        File examFile = new File(basePath + "/Handels und Gesellschaftsrecht/11000_11112222.pdf");
        response
                .setHeader("Content-Disposition", "attachment; filename=" + "Handels und Gesellschaftsrecht" + ".pdf");
        return ResponseEntity
                .ok(new FileSystemResource(examFile));
    }
}
