package pl.languagelearn.application.security;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.languagelearn.application.email.EmailService;
import pl.languagelearn.application.user.UserRegisterDto;
import pl.languagelearn.application.user.UserService;

import java.util.List;

@Controller
class RegisterController {
    private final UserService userService;
    private final EmailService emailService;

    RegisterController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    String getRegisterForm(Model model, HttpServletRequest request) {
        request.getSession().invalidate();
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        model.addAttribute("userRegisterDto", userRegisterDto);
        return "register";
    }

    @PostMapping("/register")
    String register(@Valid UserRegisterDto userRegisterDto, BindingResult bindingResult,
                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<String> validationErrors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
            return "redirect:/register";
        } else {
            try {
                userService.register(userRegisterDto.getEmail(), userRegisterDto.getPassword());
                redirectAttributes.addFlashAttribute("message", "Na adres email " + userRegisterDto.getEmail() + " przesłano link aktywacyjny.");
                return "redirect:/login";
            } catch (RuntimeException | MessagingException e) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
                return "redirect:/register";
            }
        }
    }

    @GetMapping("/register-confirm")
    String confirmRegistration(@RequestParam String token, RedirectAttributes redirectAttributes) {
        try {
            userService.confirmRegistration(token);
            redirectAttributes.addFlashAttribute("message", "Konto aktywowane.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getLocalizedMessage());
        }
        return "redirect:/login";
    }

    @GetMapping("/send-email")
    String getEmailPage() {
        return "send-email";
    }

    @PostMapping("/send-email")
    String sendConfirmationEmail(@RequestParam String username, RedirectAttributes redirectAttributes) {

        userService.findUserByEmail(username)
                .ifPresentOrElse(userDto -> {
                            if (!userDto.isAccountNotLocked()) {
                                try {
                                    emailService.sendConfirmationEmail(username, userDto.getConfirmationToken());
                                    redirectAttributes.addFlashAttribute("message", "Ponownie wysłano maila.");
                                } catch (MessagingException e) {
                                    redirectAttributes.addFlashAttribute("message", e.getLocalizedMessage());
                                }

                            } else {
                                redirectAttributes.addFlashAttribute("message", "Konto zostało już aktywowane.");
                            }
                        },
                        () -> redirectAttributes.addFlashAttribute("message", "Nie znaleziono użytkownika o podanym adresie email."));

        return "redirect:/login";


    }
}
