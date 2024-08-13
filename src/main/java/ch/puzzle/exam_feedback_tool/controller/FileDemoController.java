package ch.puzzle.exam_feedback_tool.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/v2")
public class FileDemoController {

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadFile(HttpServletResponse response) {
        System.out.println("Download file");
        System.out.println(new File(".").getAbsolutePath());
        File examFile = new File("/resources/11111_11112222.pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + "test" + ".pdf");
//        if (!examFile.exists())
//            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new FileSystemResource(examFile));
    }
}
