package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ExamFileServiceTest {

    ExamFileService examFileService;

    @Mock
    ValidationService validationService;

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
    void shouldThrow400BadRequestWhenUserInputIsInvalid() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> examFileService
                .getMatchingExams("53", "44445555"));
        assertEquals("400 BAD_REQUEST \"Ungültige Prüfungslaufnummer: 53\"", responseStatusException
                .getMessage());
    }

    @Test
    void shouldThrow404NotFoundWhenNoMatchesAreFound() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> examFileService
                .getMatchingExams("11004", "22223333"));

        assertEquals("404 NOT_FOUND \"Keine Prüfungen für die Prüfungslaufnummer 11004 gefunden\"", responseStatusException
                .getMessage());
    }
}