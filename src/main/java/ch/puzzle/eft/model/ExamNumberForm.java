package ch.puzzle.eft.model;

import jakarta.validation.constraints.Pattern;

public class ExamNumberForm {

    @Pattern(regexp = "[0-9]{5}", message = "Prüfungsnummer muss aus genau 5 Ziffern bestehen.")
    private String examNumber;


    public ExamNumberForm(String examNumber) {
        this.examNumber = examNumber;
    }

    public String getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(String examNumber) {
        this.examNumber = examNumber;
    }
}
