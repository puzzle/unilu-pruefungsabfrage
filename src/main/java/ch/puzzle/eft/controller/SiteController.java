package ch.puzzle.eft.controller;

import java.time.Duration;
import java.util.Objects;

import ch.puzzle.eft.model.ExamNumberForm;
import ch.puzzle.eft.service.ExamService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@Controller
public class SiteController {
    private final ExamService examFileService;

    private static final String SEARCH_TEMPLATE = "search";

    @Value("${test.isSecureCookie:true}")
    private boolean isSecureCookie;

    public SiteController(ExamService examFileService) {
        this.examFileService = examFileService;
    }

    @GetMapping("/")
    public String viewIndexPage(@CookieValue(value = "cookie-consent", defaultValue = "not-set") String cookiesAccepted, Model model) {
        model.addAttribute("cookiesMissing", !(Boolean.parseBoolean(cookiesAccepted)));
        model.addAttribute("examNumberForm", new ExamNumberForm(null));
        return SEARCH_TEMPLATE;
    }

    @GetMapping("/contact")
    public String viewContactPage() {
        return "contact";
    }

    @PostMapping("/")
    public String viewValidatePage(@Valid ExamNumberForm examNumberForm, BindingResult bindingResult, Model model) {
        model.addAttribute("examNumberForm", examNumberForm);
        if (!bindingResult.hasErrors()) {
            try {
                model.addAttribute("examFiles", examFileService.getMatchingExams(examNumberForm.getExamNumber()));
            } catch (ResponseStatusException e) {
                bindingResult.rejectValue("examNumber", "error.examNumberForm", Objects.requireNonNull(e.getReason()));
            }
        }
        return SEARCH_TEMPLATE;
    }


    @PostMapping("/cookies")
    public ResponseEntity<String> acceptCookies(Model model) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("cookie-consent", "true");
        cookieBuilder.maxAge(Duration.ofDays(365)
                                     .toSeconds());
        cookieBuilder.sameSite("Strict");
        cookieBuilder.httpOnly(true);
        cookieBuilder.secure(isSecureCookie);
        return ResponseEntity.status(HttpStatus.FOUND)
                             .header(HttpHeaders.SET_COOKIE,
                                     cookieBuilder.build()
                                                  .toString())
                             .header(HttpHeaders.LOCATION, "/")
                             .build();
    }
}