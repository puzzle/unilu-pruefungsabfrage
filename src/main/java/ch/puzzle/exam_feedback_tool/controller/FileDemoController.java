package ch.puzzle.exam_feedback_tool.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/exam")
public class FileDemoController {

    public FileDemoController(Environment environment) {
        this.environment = environment;
    }

    private Environment environment;

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(HttpServletResponse response) {
        String basePath = environment.getProperty("RESOURCE_DIR", "");
        File examFile = new File(basePath + "/11111_11112222.pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + "test" + ".pdf");
        return ResponseEntity.ok(new FileSystemResource(examFile));
    }
}
