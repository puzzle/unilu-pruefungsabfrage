package ch.puzzle.eft.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/all-cookies")
    public String readAllCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logInfos(cookies);
            return "home";
        } else {
            logger.info("no cookies found");
        }
        return "error";
    }

    @RequestMapping("/home")
    public String home(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
        if (principal == null) {
            logger.info("current principal is null");
            return "error";
        }

        logger.info("current principal is {}", principal.getName());
        model.addAttribute("name", principal.getName());
        model.addAttribute("email", principal.getFirstAttribute("email"));
        model.addAttribute("userAttributes", principal.getAttributes());
        return "home";
    }

    private static void logInfos(Cookie[] cookies) {
        logger.info("all my cookies are:");
        Arrays.stream(cookies)
              .forEach(c -> {
                  logger.info("--- cookie {} -----------------------------------------", c.toString());
                  logger.info("  name {}", c.getName());
                  logger.info("  domain {}", c.getDomain());
                  logger.info("  path {}", c.getPath());
                  logger.info("  attributes (size={}) {}",
                              c.getAttributes() != null
                                      ? c.getAttributes()
                                         .size()
                                      : 0,
                              c.getAttributes(),
                              c.getAttributes()
                               .toString());
                  logger.info("  value {}", c.getValue());
                  logger.info("  maxAge {}", String.valueOf(c.getMaxAge()));
                  logger.info("  secure {}", String.valueOf(c.getSecure()));
              });
    }
}
