package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ValidationService validationService;
    private static final Logger logger = Logger
            .getLogger(ExamFileService.class
                    .getName());

    @Autowired
    public ExamFileService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public List<File> getAllExamFiles() {
        File dryPath = new File("static");
        File[] subjectDirectories = dryPath
                .listFiles(File::isDirectory);
        if (subjectDirectories == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact your local admin");
        }
        return Arrays
                .stream(subjectDirectories)
                .map(e -> e
                        .listFiles(File::isFile))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toList();
    }

    public List<ExamFileModel> getMatchingExams(String searchInput) {
        if (!validationService
                .validateExamNumber(searchInput)) {
            logger
                    .warning("Validation failed for exam number: " + searchInput);
            return Collections
                    .emptyList();
        }
        List<File> matchingFiles = getAllExamFiles()
                .stream()
                .filter(file -> file
                        .getName()
                        .equals(searchInput + ".pdf"))
                .toList();
        return matchingFiles
                .stream()
                .map(ExamFileModel::new)
                .toList();
    }
}
