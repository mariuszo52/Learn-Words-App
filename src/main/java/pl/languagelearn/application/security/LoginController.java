package pl.languagelearn.application.security;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.languagelearn.application.user.UserDto;
import pl.languagelearn.application.user.UserService;

@Controller
class LoginController {
    private final UserService userService;

    LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    String login(HttpServletRequest request, Model model) {
        HttpSession httpSession = request.getSession();
        String errorMessage = (String) httpSession.getAttribute("errorMessage");
        model.addAttribute("errorMessage", errorMessage);
        httpSession.setAttribute("errorMessage", null);
        return "login";
    }

    @GetMapping("/remember-pass")
    String getRememberPassPage() {
        return "remember-pass";
    }

    @PostMapping("/remember-pass")
    String resetPass(@RequestParam String username, RedirectAttributes redirectAttributes) {
        userService.findUserByEmail(username).ifPresentOrElse(userDto -> {
                    try {
                        userService.resetPassword(userDto.getEmail());
                        redirectAttributes.addFlashAttribute("message", "Na adres email " + userDto.getEmail() + " wysłano nowe hasło.");
                    } catch (MessagingException e) {
                        redirectAttributes.addFlashAttribute("message", e.getLocalizedMessage());
                    }
                },
                () -> redirectAttributes.addFlashAttribute("message", "Nie znaleziono użytkownika z podanym adresem email."));

        return "redirect:/remember-pass";

    }
}
