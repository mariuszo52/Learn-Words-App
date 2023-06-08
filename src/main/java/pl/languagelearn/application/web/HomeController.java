package pl.languagelearn.application.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.languagelearn.application.user.UserRepository;
import pl.languagelearn.application.userRole.UserRoleRepository;
import pl.languagelearn.application.word.WordService;
import pl.languagelearn.application.word.dto.WordDto;

import java.util.List;
@Controller

public class HomeController {

    HomeController(WordService wordService) {
    }

    @GetMapping("/")
    String home() {
        return "index";

    }



}
