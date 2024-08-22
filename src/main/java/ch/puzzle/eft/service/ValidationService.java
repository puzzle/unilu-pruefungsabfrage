package ch.puzzle.eft.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private static final Pattern EXAM_NUMBER_PATTERN = Pattern
            .compile("^\\d{5}$");

    public boolean validateExamNumber(String examNumber) {
        if (examNumber == null) {
            return false;
        }
        Matcher matcher = EXAM_NUMBER_PATTERN
                .matcher(examNumber);
        return matcher
                .matches();
    }
}
