package pl.languagelearn.application.word;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.languagelearn.application.category.CategoryDto;
import pl.languagelearn.application.category.CategoryService;
import pl.languagelearn.application.language.LanguageService;
import pl.languagelearn.application.word.dto.WordDto;
import pl.languagelearn.application.word.dto.WordEditDto;

import java.util.List;

@Controller
public class WordEditController {
    private final LanguageService languageService;
    private final WordService wordService;
    private final CategoryService categoryService;

    public WordEditController(LanguageService languageService, WordService wordService, CategoryService categoryService) {
        this.languageService = languageService;
        this.wordService = wordService;
        this.categoryService = categoryService;
    }

    @GetMapping("/word-edit/{id}")
    String getEditWordPage(@PathVariable long id, Model model){
        WordDto wordDto = wordService.findWordById(id).orElseThrow();
        model.addAttribute("editedWord", wordDto);
        model.addAttribute("word", new WordEditDto());
        List<String> allCategoryNames = categoryService.findAllUserCategories().stream()
                .map(CategoryDto::getName)
                .toList();
        model.addAttribute("allCategoryNames", allCategoryNames);
        model.addAttribute("allLanguages", languageService.getAllLanguages());
        return "word-edit";
    }
    @PatchMapping("/word-edit/{id}")
    String editWord(@PathVariable long id, WordEditDto wordEditDto, RedirectAttributes redirectAttributes) {
        try {
            wordService.editWord(id, wordEditDto);
            redirectAttributes.addFlashAttribute("message", "Słówko zostało edytowane.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Nie udało się edytować słówka. " + e.getMessage());
        }
        return "redirect:/dictionary";
    }

    @DeleteMapping("/word-delete/{id}")
    String deleteWord(@PathVariable long id, RedirectAttributes redirectAttributes){
        try{
            wordService.deleteWord(id);
            redirectAttributes.addFlashAttribute("message", "Słówko zostało usunięte.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/dictionary";
    }
}
