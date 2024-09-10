package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ExamFileService {
    private static final Logger logger = LoggerFactory.getLogger(ExamFileService.class);
    private final Environment environment;
    private final ValidationService validationService;

    public ExamFileService(Environment environment, ValidationService validationService) {
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

    public List<ExamFileModel> getMatchingExams(String examNumber, String matriculationNumber) {
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
        return matchingFiles.stream().map(ExamFileModel::new).toList();
    }

    protected String getBasePath() { return environment.getProperty("RESOURCE_DIR"); }

    protected File[] getSubjectDirectories() {
        File baseDir = new File(getBasePath());
        return baseDir.listFiles(File::isDirectory);
    }
}
