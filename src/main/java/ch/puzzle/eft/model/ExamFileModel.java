package ch.puzzle.eft.model;

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
        return "Unknown";
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

