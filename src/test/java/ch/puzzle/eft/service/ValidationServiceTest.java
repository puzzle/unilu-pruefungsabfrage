package ch.puzzle.eft.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class ValidationServiceTest {
    ValidationService validationService;

    @Autowired
    public ValidationServiceTest(ValidationService validationService) {
        this.validationService = validationService;
    }

    @ParameterizedTest
    @ValueSource(strings = {"11000", "12000", "40503", "49291", "56302", "10203", "40302", "30122"})
    void shouldAcceptValidExamNumbers(String examNumber) {
        assertTrue(validationService.validateExamNumber(examNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"4", "30", "384", "9230", "203030", "3002039", "38282380", "2940119304"})
    void shouldNotAcceptExamNumbersWithMoreOrLessThan5Digits(String examNumber) {
        assertFalse(validationService.validateExamNumber(examNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a4040", "3k392", "85c29", "203G3", "3852N", "I am Text", "asdfg", "null", "false", "true", "nil", "", "^[0-9]{5}$"})
    void shouldNotAcceptValuesContainingLetters(String examNumber) {
        assertFalse(validationService.validateExamNumber(examNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\", ".", "[", "]", "{", "}", "(", ")", "<", ">", "*", "+", "-", "=", "!", "?", "^", "$", "|", "`", "'", "´", "\"", "~", "%", "&", ",", "_", ";", ":", "°", "@", "#"})
    void shouldNotAcceptValuesContainingSpecialCharacters(String specialCharacter) {
        String examNumber = "1103" + specialCharacter;
        assertFalse(validationService.validateExamNumber(examNumber));
    }

    @Test
    void shouldNotAcceptNullValue() {
        assertFalse(validationService.validateExamNumber(null));
    }
}
