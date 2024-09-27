package ch.puzzle.eft.controller;

import ch.puzzle.eft.model.ExamNumberForm;
import ch.puzzle.eft.service.ExamService;
import jakarta.validation.Valid;
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

import java.util.Objects;

import java.time.Duration;
import java.util.Objects;

@Controller
public class SiteController {

    private static final String SEARCH_TEMPLATE = "search";
    private final ExamService examFileService;

    public SiteController(ExamService examFileService) {
        this.examFileService = examFileService;
    }

    @GetMapping("/")
    public String viewIndexPage(@CookieValue(value = "cookie-consent", defaultValue = "not-set") String cookiesAccepted, Model model) {
        model.addAttribute("cookiesMissing", !(Boolean.parseBoolean(cookiesAccepted)));
        return "index";
    }

    @GetMapping("/search")
    public String viewSearchPage(Model model) {
        model.addAttribute("examNumberForm", new ExamNumberForm(null));
        return SEARCH_TEMPLATE;
    }

    @PostMapping("/search")
    public String viewValidatePage(@Valid ExamNumberForm examNumberForm, BindingResult bindingResult, Model model) {
        model.addAttribute("examNumberForm", examNumberForm);
        if (!bindingResult.hasErrors()) {
            try {
                // TODO: Replace hardcoded marticulationNumber 11112222 with dynamic number after login is implemented
                model.addAttribute("examFiles",
                                   examFileService.getMatchingExams(examNumberForm.getExamNumber(), "11112222"));
            } catch (ResponseStatusException e) {
                bindingResult.rejectValue("examNumber", "error.examNumberForm", Objects.requireNonNull(e.getReason()));
            }
        }
        return SEARCH_TEMPLATE;
    }


    @PostMapping("/")
    public ResponseEntity<String> acceptCookies(Model model) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("cookie-consent", "true");
        cookieBuilder.maxAge(Duration.ofDays(365)
                                     .toSeconds());
        cookieBuilder.sameSite("Strict");
        cookieBuilder.httpOnly(true);
        //        cookieBuilder.secure(true); TODO: do as soon as login is in main
        return ResponseEntity.status(HttpStatus.FOUND)
                             .header(HttpHeaders.SET_COOKIE,
                                     cookieBuilder.build()
                                                  .toString())
                             .header(HttpHeaders.LOCATION, "/")
                             .build();
    }
}