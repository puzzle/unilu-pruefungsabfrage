package ch.puzzle.exam_feedback_tool.controller;


import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/home")
    public String home(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
        if (principal == null) {
            // return "redirect:/login";
            return "home";
        }
        model.addAttribute("name", principal.getName());
        model.addAttribute("email", principal.getFirstAttribute("email"));
        model.addAttribute("userAttributes", principal.getAttributes());
        return "home";
    }

}