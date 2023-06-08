package pl.languagelearn.application.word;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.languagelearn.application.language.LanguageDto;
import pl.languagelearn.application.language.LanguageService;
import pl.languagelearn.application.word.dto.WordDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class DictionaryController {
    private final WordService wordService;
    private final LanguageService languageService;

    DictionaryController(WordService wordService, LanguageService languageService) {
        this.wordService = wordService;
        this.languageService = languageService;
    }

    @GetMapping("/dictionary")
    public String getDictionary(@RequestParam (defaultValue = "1") int page,
                                @RequestParam (defaultValue = "50") int size,
                                @RequestParam(defaultValue = "polishName") String sort,
                                @RequestParam (defaultValue = "angielski") String language,
                                @RequestParam (required = false) String wordName,
                                Model model) {
        Sort sorting = Sort.by(sort);
        Pageable pageable = PageRequest.of(page - 1, size, sorting);
        Page<WordDto> allUserWords = wordName == null ? wordService.getAllUserWords(pageable, language) : wordService.getFilteredUserWords(pageable, wordName);
        if(allUserWords.isEmpty()){
            model.addAttribute("message", "Nie znaleziono");
        }
        model.addAttribute("dictionary", allUserWords);
        model.addAttribute("totalPages", allUserWords.getTotalPages());
        int totalPages = allUserWords.getTotalPages();
        List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
        List<String> allLanguages = languageService.getAllLanguages().stream()
                .map(LanguageDto::getName)
                .toList();
       model.addAttribute("languages", allLanguages);
       model.addAttribute("pageNumbers", pageNumbers);
       model.addAttribute("page", page);
       model.addAttribute("size", size);
       model.addAttribute("sort", sort);
       model.addAttribute("language", language);
       model.addAttribute("wordName", wordName);
        return "dictionary";


    }
}
