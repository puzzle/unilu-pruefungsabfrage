package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExamFileServiceTest {

    @Spy
    ExamFileService examFileService;

    @BeforeEach
    void setUp() {
        when(examFileService
                .getBasePath())
                .thenReturn("static");
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
    void shouldThrowExceptionWhenDirectoryIsEmpty() {
        when(examFileService
                .getSubjectDirectories())
                .thenReturn(new File[0]);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> examFileService
                .getAllExamFiles());
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException
                .getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenDirectoryIsNonExistent() {
        when(examFileService
                .getBasePath())
                .thenReturn("nonExistentDirectory");
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> examFileService
                .getAllExamFiles());
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException
                .getStatusCode());
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
    void shouldThrowExceptionWhenExamNumberInputIsInvalid() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> examFileService
                .getMatchingExams("53", "44445555"));
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException
                .getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenNoMatchingExamsAreFound() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> examFileService
                .getMatchingExams("11004", "22223333"));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException
                .getStatusCode());
    }


}