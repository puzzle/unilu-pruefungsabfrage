package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExamService {
    private static final Logger logger = LoggerFactory.getLogger(ExamService.class);
    private final Environment environment;
    private final ValidationService validationService;

    public ExamService(Environment environment, ValidationService validationService) {
        this.environment = environment;
        this.validationService = validationService;
    }

    public List<File> getAllExamFiles() {
        File[] subjectDirectories = getSubjectDirectories();
        if (subjectDirectories == null || subjectDirectories.length == 0) {
            logger.info("No Subdirectories in path {} found", getBasePath());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                              String.format("Keine Unterordner im Pfad %s gefunden", getBasePath()));
        }
        return Arrays.stream(subjectDirectories)
                     .map(e -> e.listFiles(File::isFile))
                     .filter(Objects::nonNull)
                     .flatMap(Arrays::stream)
                     .toList();
    }

    public List<ExamModel> getMatchingExams(String examNumber, String matriculationNumber) {
        if (!validationService.validateExamNumber(examNumber)) {
            logger.info("Invalid Exam Number: {}", examNumber);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                              String.format("Ungültige Prüfunslaufnummer: %s", examNumber));
        }
        List<File> matchingFiles = getAllExamFiles().stream()
                                                    .filter(file -> file.getName()
                                                                        .equals(String.format("%s_%s.pdf",
                                                                                              examNumber,
                                                                                              matriculationNumber)))
                                                    .toList();
        if (matchingFiles.isEmpty()) {
            logger.info("No exam with the number {} found", examNumber);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                              String.format("Keine Prüfungen für die Prüfungslaufnummer %s gefunden",
                                                            examNumber));
        }
        Collator collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.PRIMARY);

        return matchingFiles.stream()
                            .map(ExamModel::new)
                            .sorted((e1, e2) -> collator.compare(e1.getSubjectName(), e2.getSubjectName()))
                            .toList();
    }

    protected String getBasePath() {
        return environment.getProperty("RESOURCE_DIR");
    }

    protected File[] getSubjectDirectories() {
        File baseDir = new File(getBasePath());
        return baseDir.listFiles(File::isDirectory);
    }

    public File getFileToDownload(String subjectName, String filename) {
        File examToDownload = new File(getBasePath() + "/" + subjectName + "/" + filename);
        if (!examToDownload.exists()) {
            logger.info("No file found for subject {} and filename {}", subjectName, filename);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                              String.format("Kein File für Fach %s und Filename %s",
                                                            subjectName,
                                                            filename));
        }
        return examToDownload;
    }

    public ByteArrayOutputStream convertFilesToZip(List<ExamModel> examFileList) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (ExamModel examFile : examFileList) {
                String name = examFile.getSubjectName() + ".pdf";
                ZipEntry zipEntry = new ZipEntry(name);
                zos.putNextEntry(zipEntry);

                try (FileInputStream fin = new FileInputStream(examFile.getFile())) {
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fin.read(bytes)) >= 0) {
                        zos.write(bytes, 0, length);
                    }
                }

                zos.closeEntry();
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return outputStream;
    }

    public ByteArrayOutputStream convertSelectedFilesToZip(String examNumber) {
        // TODO: Replace hardcoded marticulationNumber 11112222 with dynamic number after login is implemented
        List<ExamModel> matchingExams = getMatchingExams(examNumber, "11112222");
        return convertFilesToZip(matchingExams);
    }
}
