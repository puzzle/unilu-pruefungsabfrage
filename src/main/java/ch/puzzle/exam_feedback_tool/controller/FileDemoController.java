package ch.puzzle.exam_feedback_tool.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v2")
public class FileDemoController {

    public FileDemoController(Environment environment) {
        this.environment = environment;
    }

    private Environment environment;

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
