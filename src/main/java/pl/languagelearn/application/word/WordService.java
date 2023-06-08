package pl.languagelearn.application.word;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.category.CategoryRepository;
import pl.languagelearn.application.exception.CannotEditException;
import pl.languagelearn.application.exception.CategoryNotFoundException;
import pl.languagelearn.application.exception.LanguageNotFoundException;
import pl.languagelearn.application.exception.WordNotFoundException;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.language.LanguageRepository;
import pl.languagelearn.application.user.UserService;
import pl.languagelearn.application.word.dto.WordDto;
import pl.languagelearn.application.word.dto.WordEditDto;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WordService {
    private final WordRepository wordRepository;
    private final CategoryRepository categoryRepository;
    private final LanguageRepository languageRepository;

    WordService(WordRepository wordRepository, CategoryRepository categoryRepository, LanguageRepository languageRepository) {
        this.wordRepository = wordRepository;
        this.categoryRepository = categoryRepository;
        this.languageRepository = languageRepository;
    }

    public List<WordDto> findAllWords() {
        Iterable<Word> all = wordRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(WordMapper::map)
                .collect(Collectors.toList());

    }

    public Optional<WordDto> findWordById(Long id) {
        return wordRepository.findById(id)
                .map(WordMapper::map);

    }

    public boolean isImageLinkValid(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    public Page<WordDto> getAllUserWords(Pageable pageable, String language) {
        Long loggedUserId = UserService.getLoggedUserId();
        return wordRepository.findAllByUser_idAndLanguage_Name(loggedUserId, language, pageable)
                .map(WordMapper::map);
    }

    public Page<WordDto> getFilteredUserWords(Pageable pageable, String wordName) {
        Long loggedUserId = UserService.getLoggedUserId();
        Page<Word> collect = wordRepository.findAllByUser_id(loggedUserId, pageable);
        List<WordDto> list = collect.getContent().stream()
                .map(WordMapper::map)
                .filter(wordDto -> filterWords(wordDto, wordName))
                .collect(Collectors.toList());
        return new PageImpl<>(list);
    }

    private boolean filterWords(WordDto wordDto, String wordName) {
        return StringUtils.containsIgnoreCase(
                wordDto.getPolishName(), wordName)
                || StringUtils.containsIgnoreCase(wordDto.getTranslation(), wordName)
                || StringUtils.containsIgnoreCase(wordDto.getSentence(), wordName);
    }

    public Set<String> getUserLanguagesNames(Long userId) {
        return wordRepository.findAllByUser_id(userId).stream()
                .map(word -> word.getLanguage().getName())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void editWord(long id, WordEditDto wordEditDto) {
        Word word = wordRepository.findById(id)
                .orElseThrow(WordNotFoundException::new);
        word.setPolishName(wordEditDto.getPolishName());
        word.setTranslation(wordEditDto.getTranslation());
        word.setImageLink(wordEditDto.getImageLink());
        word.setSentence(wordEditDto.getSentence());
        Category category = categoryRepository.findByNameAndUser_Id(wordEditDto.getCategoryName(),
                UserService.getLoggedUserId()).orElseThrow(CategoryNotFoundException::new);
        word.setCategory(category);
        Language language = languageRepository.findByName(wordEditDto.getLanguageName())
                .orElseThrow(LanguageNotFoundException::new);
        word.setLanguage(language);
    }

    public void deleteWord(long id) {
        List<Long> allUserWordsIds = wordRepository.findAllByUser_id(UserService.getLoggedUserId()).stream()
                .map(Word::getId)
                .toList();
        Word word = wordRepository.findById(id)
                .orElseThrow(WordNotFoundException::new);

        if (allUserWordsIds.contains(word.getId())) {
            wordRepository.deleteById(id);
        } else {
            throw new CannotEditException("Nie można usunąć słowa o podanym ID.");
        }

    }
}
