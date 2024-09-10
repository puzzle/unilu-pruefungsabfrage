package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ExamFileServiceTest {

    @Spy
    ExamFileService examFileService;

    @BeforeEach
    void setUp() {
        when(examFileService.getBasePath()).thenReturn("static");
    }

    @Autowired
    public ExamFileServiceTest(ExamFileService examFileService) {
        this.examFileService = examFileService;
    }

    @Test
    void shouldReturnAllExamFiles() {

        List<File> result = examFileService.getAllExamFiles();

        List<File> filesToCheck = List.of(Paths.get("static", "Handels und Gesellschaftsrecht", "11000_11112222.pdf")
                                               .toFile(),
                                          Paths.get("static", "Privatrecht", "11000_11112222.pdf")
                                               .toFile(),
                                          Paths.get("static", "Strafrecht", "11000_11112222.pdf")
                                               .toFile(),
                                          Paths.get("static", "Öffentliches Recht", "11000_11112222.pdf")
                                               .toFile());

        assertEquals(22, result.size());
        for (File fileToCheck : filesToCheck) {
            assertTrue(result.contains(fileToCheck));
        }
    }

    @Test
    void shouldThrowExceptionWhenDirectoryIsEmpty() {
        when(examFileService.getSubjectDirectories()).thenReturn(new File[0]);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                                                                       () -> examFileService.getAllExamFiles());
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenDirectoryIsNonExistent() {
        when(examFileService.getBasePath()).thenReturn("nonExistentDirectory");
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                                                                       () -> examFileService.getAllExamFiles());
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
    }

    @Test
    void shouldReturnMatchingExamNamesAndAmount() {
        List<String> filesToCheck = List.of("static/Handels und Gesellschaftsrecht/11001_22223333.pdf",
                                            "static/Privatrecht/11001_22223333.pdf",
                                            "static/Strafrecht/11001_22223333.pdf",
                                            "static/Öffentliches Recht/11001_22223333.pdf");

        List<String> expectedFileNames = filesToCheck.stream()
                                                     .map(p -> Paths.get(p)
                                                                    .toFile())
                                                     .map(ExamFileModel::new)
                                                     .map(ExamFileModel::getFileName)
                                                     .toList();

        List<ExamFileModel> result = examFileService.getMatchingExams("11001", "22223333");

        List<String> resultFileNames = result.stream()
                                             .map(ExamFileModel::getFileName)
                                             .toList();

        assertEquals(resultFileNames, expectedFileNames);
        assertIterableEquals(expectedFileNames, resultFileNames, "The lists of exam file names should match");
    }

    @Test
    void shouldThrowExceptionWhenExamNumberInputIsInvalid() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                                                                       () -> examFileService.getMatchingExams("53",
                                                                                                              "44445555"));
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenNoMatchingExamsAreFound() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                                                                       () -> examFileService.getMatchingExams("11004",
                                                                                                              "22223333"));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
    }

    @Test
    void shouldThrowExceptionWhenNoExamsIsFound() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                                                                       () -> examFileService.getFileToDownload("Privatrecht",
                                                                                                               "11000_22223333.pdf"));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
    }

    @Test
    void shouldReturnFileToDownload() {
        File result = examFileService.getFileToDownload("Privatrecht", "11001_22223333.pdf");
        assertEquals(new File("static/Privatrecht/11001_22223333.pdf"), result);
    }
}