package pl.languagelearn.application.word.addWord;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.languagelearn.application.category.CategoryDto;
import pl.languagelearn.application.category.CategoryService;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.user.UserStatsService;
import pl.languagelearn.application.word.dto.WordDto;

import java.io.IOException;
import java.util.List;

@Controller
public class AddWordController {
    private final AddWordService addWordService;
    private final CategoryService categoryService;
    private final UserStatsService userStatsService;
    public static final String MESSAGE_ATTRIBUTE = "message";

    AddWordController(AddWordService addWordService, CategoryService categoryService, UserStatsService userStatsService) {
        this.addWordService = addWordService;
        this.categoryService = categoryService;
        this.userStatsService = userStatsService;
    }

    @GetMapping("/learn/add")
    String getAddForm(Model model) {
        List<Language> allLanguages = addWordService.getAllLanguages();
        model.addAttribute("languages", allLanguages);
        WordDto wordDto = new WordDto();
        model.addAttribute("wordDto", wordDto);
        List<CategoryDto> categories = categoryService.findAllUserCategories();
        model.addAttribute("categories", categories);
        userStatsService.setLearnedWordsStats();
        return "add-word";
    }

    @PostMapping("/learn/add")
    String addWord(WordDto wordDto, RedirectAttributes redirectAttributes, Model model) {
            List<Language> allLanguages = addWordService.getAllLanguages();
            model.addAttribute("languages", allLanguages);
        BeanPropertyBindingResult errors = addWordService.wordValidate(wordDto);
        if(!errors.hasErrors()) {
            addWordService.addWord(wordDto);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Dodano słówko");
            return "redirect:/learn/add";
        }else {
            for (FieldError error: errors.getFieldErrors()){
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "add-word";
        }
    }
    @PostMapping("/learn/add/file")
    String addWordsFile(
            @RequestParam String language,
            @RequestParam("wordsListFile") MultipartFile multipartFile,
            RedirectAttributes redirectAttributes) {
        try {
            if(addWordService.isFileFormatValid(multipartFile)){
                addWordService.addWordsList(multipartFile, language);
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Dodano plik");
            }
            else {
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Nieprawidłowy format pliku");
            }
            } catch (IOException | RuntimeException e) {
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, e.getMessage());
            }
        return "redirect:/learn/add";
    }
    @PostMapping("/learn/add/text")
    String addWordText(
            @RequestParam String language,
            @RequestParam String wordsText,
            RedirectAttributes redirectAttributes) {
            try{
                addWordService.addWordsFromText(wordsText, language);
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "Dodano słowa");
            }catch (RuntimeException e){
                redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, e.getMessage());
            }

        return "redirect:/learn/add";
    }


        }


