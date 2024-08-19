package ch.puzzle.eft.service;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    public boolean validateExamNumber(String examNumber) {
        String examNumberRegex = "^[0-9]{5}$";
        return examNumber
                .matches(examNumberRegex);
    }
}
