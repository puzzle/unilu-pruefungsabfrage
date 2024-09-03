package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class ExamFileService {
    private final Environment environment;
    private final ValidationService validationService;
    private static final Logger logger = Logger
            .getLogger(ExamFileService.class
                    .getName());

    @Autowired
    public ExamFileService(Environment environment, ValidationService validationService) {
        this.environment = environment;
        this.validationService = validationService;
    }


    public List<File> getAllExamFiles() {
        String basePath = environment
                .getProperty("RESOURCE_DIR", "");
        File dryPath = new File(basePath);
        File[] subjectDirectories = dryPath
                .listFiles(File::isDirectory);
        if (subjectDirectories == null) {
            logger
                    .info("No exam files found in " + dryPath);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please contact your local admin");
        }
        return Arrays
                .stream(subjectDirectories)
                .map(e -> e
                        .listFiles(File::isFile))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toList();
    }

    public List<ExamFileModel> getMatchingExams(String searchInput, String matriculationNumber) {
        if (!validationService
                .validateExamNumber(searchInput)) {
            logger
                    .info("Validation failed for exam number: " + searchInput);
            return Collections
                    .emptyList();
        }
        List<File> matchingFiles = getAllExamFiles()
                .stream()
                .filter(file -> file
                        .getName()
                        .equals(searchInput + "_" + matriculationNumber + ".pdf"))
                .toList();
        if (matchingFiles
                .isEmpty()) {
            logger
                    .info("No matching files found under matriculation number for exam number: " + searchInput);
        }
        return matchingFiles
                .stream()
                .map(ExamFileModel::new)
                .toList();
    }
}
