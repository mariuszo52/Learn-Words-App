package pl.languagelearn.application.word.learnWords;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.languagelearn.application.answer.Answer;
import pl.languagelearn.application.category.CategoryComparator;
import pl.languagelearn.application.category.CategoryDto;
import pl.languagelearn.application.category.CategoryService;
import pl.languagelearn.application.exception.WordNotFoundException;
import pl.languagelearn.application.user.UserService;
import pl.languagelearn.application.user.UserStatsService;
import pl.languagelearn.application.word.WordService;
import pl.languagelearn.application.word.addWord.AddWordController;
import pl.languagelearn.application.word.dto.WordDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class LearnWordsController {
    private final LearnWordsService learnWordsService;
    private final CategoryService categoryService;
    private final WordService wordService;
    private final UserStatsService userStatsService;
    private static final String GOOD_ANSWER = "good";

    LearnWordsController(LearnWordsService learnWordsService, CategoryService categoryService, WordService wordService, UserStatsService userStatsService) {
        this.learnWordsService = learnWordsService;
        this.categoryService = categoryService;
        this.wordService = wordService;
        this.userStatsService = userStatsService;
    }


    @GetMapping("/learn")
    String showListOfWords() {
        return "learn";
    }

    //cos tu zle
    @GetMapping("/learn/repeat")
    String repeatWords(@RequestParam(required = false) Optional<String> value, @RequestParam(defaultValue = "angielski") String language, HttpServletRequest request, Model model) {
        String queryString = request.getQueryString();
        Set<String> userLanguagesNames = wordService.getUserLanguagesNames(UserService.getLoggedUserId());
        model.addAttribute("userLanguages", userLanguagesNames);
        model.addAttribute("selectedLanguage", language);
        model.addAttribute("wordsLeftToday", learnWordsService.getNumberWordsToRepeatToday(language));
        value.ifPresent(value1 -> {
            if (value1.equals(GOOD_ANSWER)) {
                userStatsService.updateRepeatedWordsCounter();
                userStatsService.setLearnedWordsStats();
            }
        });
        List<WordDto> wordsToRepeat = learnWordsService.createWordsToRepeat(language);
        learnWordsService.generateWordToRepeat(wordsToRepeat).ifPresentOrElse(word -> {
            model.addAttribute("wordToTranslate", word);
            boolean imageLinkValid = wordService.isImageLinkValid(word.getImageLink());
            model.addAttribute("isLinkValid", imageLinkValid);
            if (queryString != null && queryString.contains(GOOD_ANSWER)) {
                learnWordsService.updatePriority(word.getId());
            }
        }, () -> model.addAttribute("message", "Nie ma słów do wyświetlenia."));
        return "learn-repeat";
    }

    @GetMapping("learn/category")
    String getCategories(@RequestParam(defaultValue = "angielski") String language, @RequestParam(required = false) String categoryName, Model model) {
        Set<String> userLanguagesNames = wordService.getUserLanguagesNames(UserService.getLoggedUserId());
        model.addAttribute("userLanguages", userLanguagesNames);
        model.addAttribute("language", language);
        if (categoryName == null) {
            List<CategoryDto> allCategories = categoryService.findAllUserCategoriesByLanguage(language);
            allCategories.forEach(categoryDto -> categoryDto.setNumbersOfWordsByLanguage(categoryService.getCategoryWordsByLanguage(categoryDto, language)));
            allCategories.sort(new CategoryComparator());
            model.addAttribute("categories", allCategories);
        } else {
            List<CategoryDto> categories = categoryService.findAllUserCategoriesByLanguage(language).stream().filter(categoryDto -> StringUtils.containsIgnoreCase(categoryDto.getName(), categoryName)).toList();
            categories.forEach(categoryDto -> categoryDto.setNumbersOfWordsByLanguage(categoryService.getCategoryWordsByLanguage(categoryDto, language)));
            model.addAttribute("categories", categories);
            if (categories.isEmpty()) {
                model.addAttribute(AddWordController.MESSAGE_ATTRIBUTE, "Nie znaleziono kategorii o podanej nazwie");
                model.addAttribute("categories", categories);
            }
        }
        return "category-list";
    }

    @PostMapping("learn/category/check")
    String checkWord(@RequestParam long word, @RequestParam String answer, HttpServletRequest request) {
        WordDto wordDto = wordService.findWordById(word).orElseThrow(WordNotFoundException::new);
        boolean isGoodAnswer = learnWordsService.checkAnswer(wordDto, answer);
        learnWordsService.getRepeatedWords().add(word);
        answer = learnWordsService.setDefaultAnswer(answer);
        Answer answerToResults = new Answer(wordDto.getPolishName(), answer, isGoodAnswer, wordDto.getTranslation());
        learnWordsService.getRepeatResults().add(answerToResults);
        learnWordsService.setWordsCounter(learnWordsService.getWordsCounter() + 1);
        String referer = request.getHeader("referer");
        return "redirect:" + referer;
    }

    @GetMapping("learn/category/{id}")
    String repeatByCategory(@RequestParam(defaultValue = "angielski") String language, @RequestParam int number, @PathVariable long id, Model model, HttpServletRequest request) {
        learnWordsService.resetRepeatProgress(request);
        int counter = learnWordsService.getWordsCounter();
        double progressOfRepetition = learnWordsService.calculateProgressOfRepetition(counter, number);
        categoryService.findCategoryById(id).ifPresentOrElse(category -> {
            model.addAttribute("category", category);
            model.addAttribute("numberOfWords", number);
        }, () -> model.addAttribute("message", "Nie znaleziono kategorii."));
        if (progressOfRepetition == 100) {
            Set<Answer> repeatResults = learnWordsService.getRepeatResults();
            model.addAttribute("results", repeatResults);
            Set<Answer> goodAnswers = learnWordsService.findGoodAnswers(repeatResults);
            model.addAttribute("goodAnswers", goodAnswers);
            model.addAttribute("language", language);
            learnWordsService.setRepeat(learnWordsService.getRepeat() + 1);
            int repeat = learnWordsService.getRepeat();
            model.addAttribute("repeat", repeat);
            return "category-results";
        }
        List<Long> wordsByCategoryAndLanguage = learnWordsService.getWordsByCategoryAndLanguage(language, id);
        learnWordsService.getWordToRepeat(wordsByCategoryAndLanguage).ifPresentOrElse(wordDto -> {
            model.addAttribute("word", wordDto);
            boolean isLinkValid = wordService.isImageLinkValid(wordDto.getImageLink());
            model.addAttribute("isLinkValid", isLinkValid);
        }, () -> model.addAttribute("message", "Nie znaleziono słowa."));
        model.addAttribute("progress", progressOfRepetition);
        model.addAttribute("counter", counter);
        return "category-repeat";
    }


}





