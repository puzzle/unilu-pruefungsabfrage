package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ExamFileService {
    private final ValidationService validationService;

    private final String rootDirectoryName;

    private static final Logger logger = LoggerFactory
            .getLogger(ExamFileService.class);

    @Autowired
    public ExamFileService(ValidationService validationService, @Value("${eft.root-directory.name}") String rootDirectoryName) {
        this.validationService = validationService;
        this.rootDirectoryName = rootDirectoryName;
    }

    public List<File> getAllExamFiles() {
        File dryPath = new File(getRootDirectoryName());
        File[] subjectDirectories = dryPath
                .listFiles(File::isDirectory);
        if (subjectDirectories == null) {
            logger
                    .info("Keine Unterordner im Pfad '{}' gefunden", dryPath);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Keine Prüfungsordner gefunden");
        }
        return Arrays
                .stream(subjectDirectories)
                .map(e -> e
                        .listFiles(File::isFile))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toList();
    }

    public List<ExamFileModel> getMatchingExams(String examNumber, String matriculationNumber) {
        if (!validationService
                .validateExamNumber(examNumber)) {
            logger
                    .info("Ungültige Prüfunslaufnummer: {}", examNumber);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String
                    .format("Ungültige Prüfungslaufnummer: %s", examNumber));
        }
        List<File> matchingFiles = getAllExamFiles()
                .stream()
                .filter(file -> file
                        .getName()
                        .equals(String
                                .format("%s_%s.pdf", examNumber, matriculationNumber)))
                .toList();
        if (matchingFiles
                .isEmpty()) {
            logger
                    .info("Keine Prüfungen für die Prüfungslaufnummer {} gefunden", examNumber);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String
                    .format("Keine Prüfungen für die Prüfungslaufnummer %s gefunden", examNumber));
        }
        return matchingFiles
                .stream()
                .map(ExamFileModel::new)
                .toList();
    }

    public String getRootDirectoryName() {
        return rootDirectoryName;
    }
}
