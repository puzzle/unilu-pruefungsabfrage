package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExamFileServiceTest {

    ExamFileService examFileService;

    @BeforeEach
    void setUp() {
        System
                .setProperty("RESOURCE_DIR", "static");
    }


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
    void shouldReturnMatchingExamNamesAndAmount() {

        File file1 = Paths
                .get("static", "Handels und Gesellschaftsrecht", "11001_22223333.pdf")
                .toFile();
        File file2 = Paths
                .get("static", "Privatrecht", "11001_22223333.pdf")
                .toFile();
        File file3 = Paths
                .get("static", "Strafrecht", "11001_22223333.pdf")
                .toFile();
        File file4 = Paths
                .get("static", "Öffentliches Recht", "11001_22223333.pdf")
                .toFile();

        List<String> expectedFileNames = new ArrayList<>();
        expectedFileNames
                .add(new ExamFileModel(file1)
                        .getFileName());
        expectedFileNames
                .add(new ExamFileModel(file2)
                        .getFileName());
        expectedFileNames
                .add(new ExamFileModel(file3)
                        .getFileName());
        expectedFileNames
                .add(new ExamFileModel(file4)
                        .getFileName());

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