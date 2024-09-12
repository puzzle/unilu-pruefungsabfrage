package ch.puzzle.eft.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;

public class ExamFileModel {
    private File file;

    public ExamFileModel(File file) {
        this.file = file;
    }

    public String getSubjectName() {
        if (this.file.getParentFile() != null) {
            return this.file.getParentFile()
                            .getName();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                          String.format("No Parent Found for File %s", getFileName()));
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

