package ch.puzzle.eft.service;

import ch.puzzle.eft.model.ExamFileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ExamFileService {
    private ValidationService validationService;

    @Autowired
    public ExamFileService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public List<File> getAllExamFiles() {
        File dryPath = new File("static");
        File[] subjectDirectories = dryPath
                .listFiles(File::isDirectory);
        if (subjectDirectories == null)
            return List
                    .of();
        return Arrays
                .stream(subjectDirectories)
                .map(e -> e
                        .listFiles(File::isFile))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toList();
    }

    public List<ExamFileModel> getMatchingExams(String searchInput) {
        validationService
                .validateExamNumber(searchInput);
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
