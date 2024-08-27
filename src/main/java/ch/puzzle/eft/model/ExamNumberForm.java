package ch.puzzle.eft.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExamNumberForm {

    @Pattern(regexp = "[0-9]{5}", message = "Prüfungsnummer muss 5 Ziffern lang sein")
    private String examNumber;


    public ExamNumberForm(String examNumber) {
        this.examNumber = examNumber;
    }
    
}
