package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        List<File> filesToCheck = List
                .of(
                    Paths
                            .get("static", "Handels und Gesellschaftsrecht", "11000_11112222.pdf")
                            .toFile(), Paths
                                    .get("static", "Privatrecht", "11000_11112222.pdf")
                                    .toFile(), Paths
                                            .get("static", "Strafrecht", "11000_11112222.pdf")
                                            .toFile(), Paths
                                                    .get("static", "Öffentliches Recht", "11000_11112222.pdf")
                                                    .toFile()
                );


        assertEquals(22, result
                .size());
        for (File fileToCheck : filesToCheck) {
            assertTrue(result
                    .contains(fileToCheck));
        }
    }

    @Test
    void shouldReturnMatchingExamNamesAndAmount() {

        List<String> filesToCheck = List
                .of(
                    "static/Handels und Gesellschaftsrecht/11001_22223333.pdf", "static/Privatrecht/11001_22223333.pdf", "static/Strafrecht/11001_22223333.pdf", "static/Öffentliches Recht/11001_22223333.pdf"
                );

        List<String> expectedFileNames = filesToCheck
                .stream()
                .map(p -> Paths
                        .get(p)
                        .toFile())
                .map(ExamFileModel::new)
                .map(ExamFileModel::getFileName)
                .toList();

        List<ExamFileModel> result = examFileService
                .getMatchingExams("11001", "22223333");

        List<String> resultFileNames = result
                .stream()
                .map(ExamFileModel::getFileName)
                .toList();

        assertEquals(4, resultFileNames
                .size());
        assertIterableEquals(expectedFileNames, resultFileNames, "The lists of exam file names should match");
    }

    @Test
    void shouldReturnEmptyListWhenUserInputIsInvalid() {
        List<ExamFileModel> result = examFileService
                .getMatchingExams("53", "44445555");

        assertTrue(result
                .isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoMatchesAreFound() {
        List<ExamFileModel> result = examFileService
                .getMatchingExams("11004", "22223333");

        assertTrue(result
                .isEmpty());
    }
}