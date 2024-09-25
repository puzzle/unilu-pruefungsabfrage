package ch.puzzle.eft.model;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExamModel {
    private final File file;
    private static final Logger logger = LoggerFactory.getLogger(ExamModel.class);


    public ExamModel(File file) {
        this.file = file;
    }

    public String getSubjectName() {
        if (this.file.getParentFile() != null) {
            return this.file.getParentFile()
                            .getName();
        }
        logger.info("No parent found for file {}", getFileName());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                          String.format("Kein Überordner für Datei %s gefunden", getFileName()));
    }

    public String getDownloadPath() {
        return this.getSubjectName() + "/" + this.getFileName();
    }

    public String getFileName() {
        return this.file.getName();
    }

    public File getFile() {
        return file;
    }

}
