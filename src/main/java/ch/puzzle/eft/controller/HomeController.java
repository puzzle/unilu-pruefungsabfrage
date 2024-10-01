package ch.puzzle.eft.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequestMapping("/eft")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/all-cookies")
    public String readAllCookies(HttpServletRequest request) {
        logger.info("processing all-cookies... ");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logInfos(cookies);
            return "loggedin";
        } else {
            logger.info("no cookies found");
        }
        return "error";
    }

    @RequestMapping("/unauthorized")
    public String unauthorized(HttpServletRequest request, Model model) {
        logger.info("processing unauthorized... ");
        return processRequest(request, null, model);
    }

    @RequestMapping("/authorized")
    public String authorized(HttpServletRequest request, Model model) {
        logger.info("processing authorized... ");
        return processRequest(request, null, model);
    }

    private static String processRequest(HttpServletRequest request, Principal principal, Model model) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logInfos(cookies);
        } else {
            logger.warn("no cookies found");
        }
        if (principal == null) {
            logger.warn("current principal is null");
        } else {
            logger.info("current principal is {}", principal.getName());
        }
        return "loggedin";
    }

    private static void logInfos(Cookie[] cookies) {
        logger.info("all my cookies are (size={}):", cookies.length);
        Arrays.stream(cookies)
              .filter(HomeController::filterShibbolethSession)
              .forEach(cookie -> {
                  logger.info("--- cookie {} -----------------------------------------", cookie);
                  logger.info("  name {}", cookie.getName());
                  logger.info("  domain {}", cookie.getDomain());
                  logger.info("  path {}", cookie.getPath());
                  logger.info("  attributes (size={}) {}",
                              (cookie.getAttributes() != null
                                      ? cookie.getAttributes()
                                              .size()
                                      : 0),
                              cookie.getAttributes()
                                    .toString());
                  logger.info("  value {}", cookie.getValue());
                  logger.info("  maxAge {}", cookie.getMaxAge());
                  logger.info("  secure {}", cookie.getSecure());

              });
    }

    private static boolean filterShibbolethSession(Cookie cookie) {
        if (cookie == null) {
            return false;
        }
        return cookie.getName()
                     .contains("shibsession") //
                || cookie.getName()
                         .equals("JSESSIONID");
    }
}
