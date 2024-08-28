package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ExamFileServiceTest {

    ExamFileService examFileService;

    @Autowired
    public ExamFileServiceTest(ExamFileService examFileService) {
        this.examFileService = examFileService;
    }

    @Test
    void shouldReturnAllExamFiles() {

        List<File> result = examFileService
                .getAllExamFiles();

        File file1 = Paths
                .get("static", "Handels und Gesellschaftsrecht", "11000_11112222.pdf")
                .toFile();
        File file2 = Paths
                .get("static", "Privatrecht", "11000_11112222.pdf")
                .toFile();
        File file3 = Paths
                .get("static", "Strafrecht", "11000_11112222.pdf")
                .toFile();
        File file4 = Paths
                .get("static", "Öffentliches Recht", "11000_11112222.pdf")
                .toFile();

        assertEquals(22, result
                .size());
        assertTrue(result
                .contains(file1));
        assertTrue(result
                .contains(file2));
        assertTrue(result
                .contains(file3));
        assertTrue(result
                .contains(file4));
    }

    @Test
    void shouldReturnMatchingExams() {

        List<ExamFileModel> result = examFileService
                .getMatchingExams("11001_22223333");

        assertEquals(4, result
                .size());


//        for (ExamFileModel examFile : result) {
//            String fileName = examFile
//                    .getFileName();
//            assertEquals("11001_22223333", fileName);
//        }
    }
}