package ch.puzzle.eft.model;

import java.io.File;

public class ExamFileModel {
    private File file;

    public ExamFileModel(File file) {
        this.file = file;
    }

    public String getSubjectName() {
        return this.file.getParentFile()
                        .getName();
    }

    public String getFileName() {
        return this.file.getName();
    }

    public File getFile() {
        return file;
    }

}

