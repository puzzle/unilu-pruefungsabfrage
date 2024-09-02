package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
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

    @Test
    void shouldConvertFilesToZip() throws IOException {
        ExamFileModel fileModel1 = mock(ExamFileModel.class);
        ExamFileModel fileModel2 = mock(ExamFileModel.class);

        File tempFile1 = File
                .createTempFile("testFile1", ".pdf");
        Files
                .write(tempFile1
                        .toPath(), "Test Content 1"
                                .getBytes());

        File tempFile2 = File
                .createTempFile("testFile2", ".pdf");
        Files
                .write(tempFile2
                        .toPath(), "Test Content 2"
                                .getBytes());

        when(fileModel1
                .getSubjectName())
                .thenReturn("Privatrecht");
        when(fileModel1
                .getFileName())
                .thenReturn("Exam1.pdf");
        when(fileModel1
                .getFile())
                .thenReturn(tempFile1);

        when(fileModel2
                .getSubjectName())
                .thenReturn("Strafrecht");
        when(fileModel2
                .getFileName())
                .thenReturn("Exam2.pdf");
        when(fileModel2
                .getFile())
                .thenReturn(tempFile2);

        List<ExamFileModel> examFileList = Arrays
                .asList(fileModel1, fileModel2);

        // Mock ServletOutputStream
        MockServletOutputStream mockOutputStream = new MockServletOutputStream();

        // Execute the service method
        examFileService
                .convertFilesToZip(examFileList, mockOutputStream);

        // Verify output
        ByteArrayInputStream bis = new ByteArrayInputStream(mockOutputStream
                .getContentAsByteArray());
        ZipInputStream zis = new ZipInputStream(bis);
        ZipEntry entry;

        String[] expectedEntries = {"Privatrecht_Exam1.pdf", "Strafrecht_Exam2.pdf"};
        String[] actualEntries = new String[2];

        String expectedContent1 = "Test Content 1";
        String expectedContent2 = "Test Content 2";

        int i = 0;
        while ((entry = zis
                .getNextEntry()) != null) {
            actualEntries[i] = entry
                    .getName();

            // Read the content of the file inside the ZIP
            byte[] fileContent = zis
                    .readAllBytes();
            String actualContent = new String(fileContent);

            // Verify the content of each file
            if (i == 0) {
                assertEquals(expectedContent1, actualContent);
            } else {
                assertEquals(expectedContent2, actualContent);
            }

            i++;
        }

        // Verify that the correct entries were added to the ZIP file
        assertArrayEquals(expectedEntries, actualEntries);
    }
}