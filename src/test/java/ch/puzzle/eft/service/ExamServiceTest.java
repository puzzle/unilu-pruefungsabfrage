package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamModel;
import ch.puzzle.eft.test.MockServletOutputStream;
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
import static org.mockito.Mockito.*;

@SpringBootTest
class ExamServiceTest {

    @Spy
    ExamService examFileService;

    MockServletOutputStream mockOutputStream = new MockServletOutputStream();

    @Autowired
    public ExamServiceTest(ExamService examFileService) {
        this.examFileService = examFileService;
    }

    @BeforeEach
    void setUp() {
        when(examFileService.getBasePath()).thenReturn("static");
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
                                                     .map(ExamModel::new)
                                                     .map(ExamModel::getFileName)
                                                     .toList();

        List<ExamModel> result = examFileService.getMatchingExams("11001", "22223333");

        List<String> resultFileNames = result.stream()
                                             .map(ExamModel::getFileName)
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
        ExamModel fileModel1 = mock(ExamModel.class);
        ExamModel fileModel2 = mock(ExamModel.class);

        File tempFile1 = File.createTempFile("testFile1", ".pdf");
        Files.write(tempFile1.toPath(), "Test Content 1".getBytes());

        File tempFile2 = File.createTempFile("testFile2", ".pdf");
        Files.write(tempFile2.toPath(), "Test Content 2".getBytes());

        when(fileModel1.getSubjectName()).thenReturn("Privatrecht");
        when(fileModel1.getFile()).thenReturn(tempFile1);

        when(fileModel2.getSubjectName()).thenReturn("Strafrecht");
        when(fileModel2.getFile()).thenReturn(tempFile2);

        List<ExamModel> examFileList = Arrays.asList(fileModel1, fileModel2);

        examFileService.convertFilesToZip(examFileList, mockOutputStream);

        ByteArrayInputStream bis = new ByteArrayInputStream(mockOutputStream.getContentAsByteArray());
        ZipInputStream zis = new ZipInputStream(bis);
        ZipEntry entry;

        String[] expectedEntries = {"Privatrecht.pdf", "Strafrecht.pdf"};
        String[] actualEntries = new String[2];


        List<String> expectedEntriesList = List.of("Test Content 1", "Test Content 2");

        int i = 0;
        while ((entry = zis.getNextEntry()) != null) {
            actualEntries[i] = entry.getName();

            byte[] fileContent = zis.readAllBytes();
            String actualContent = new String(fileContent);

            assertEquals(expectedEntriesList.get(i), actualContent);

            i++;
        }

        assertArrayEquals(expectedEntries, actualEntries);
    }

    @Test
    void shouldReturnCorrectFilesAfterZip() throws IOException {
        MockServletOutputStream mockOutputStream = new MockServletOutputStream();

        examFileService.convertSelectedFilesToZip("11000", mockOutputStream);

        // Convert the output stream's content to a ZipInputStream to read and verify the ZIP contents
        byte[] zipContent = mockOutputStream.getContentAsByteArray();
        ZipInputStream zipInputStream = new ZipInputStream(new java.io.ByteArrayInputStream(zipContent));

        ZipEntry entry;
        List<String> expectedFileNames = List.of("Privatrecht.pdf",
                                                 "Strafrecht.pdf",
                                                 "Öffentliches Recht.pdf",
                                                 "Handels und Gesellschaftsrecht.pdf");
        int fileCount = 0;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            assertTrue(expectedFileNames.contains(entry.getName()), "Unexpected file in ZIP: " + entry.getName());
            fileCount++;
        }

        assertEquals(expectedFileNames.size(), fileCount, "Not all files were zipped correctly.");
    }

    @Test
    void shouldSortExamList() {
        List<ExamModel> result = examFileService.getMatchingExams("11001", "22223333");

        List<String> expectedFileNames = List.of("Handels und Gesellschaftsrecht",
                                                 "Privatrecht",
                                                 "Strafrecht",
                                                 "Öffentliches Recht");

        for (int i = 0; i < expectedFileNames.size(); i++) {
            assertEquals(expectedFileNames.get(i),
                         result.get(i)
                               .getSubjectName());
        }
    }
}