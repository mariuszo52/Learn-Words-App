package pl.languagelearn.application.word.addWord;

import jakarta.validation.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.category.CategoryRepository;
import pl.languagelearn.application.exception.UserNotFoundException;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.language.LanguageRepository;
import pl.languagelearn.application.user.CurrentUser;
import pl.languagelearn.application.user.User;
import pl.languagelearn.application.user.UserRepository;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordRepository;
import pl.languagelearn.application.word.dto.WordDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
class AddWordService {
private final WordRepository wordRepository;
private final CategoryRepository categoryRepository;
private final LanguageRepository languageRepository;
private final Validator validator;
private final CurrentUser currentUser;

    AddWordService(WordRepository wordRepository, CategoryRepository categoryRepository,
                   LanguageRepository languageRepository, Validator validator, CurrentUser currentUser) {
        this.wordRepository = wordRepository;
        this.categoryRepository = categoryRepository;
        this.languageRepository = languageRepository;
        this.validator = validator;
        this.currentUser = currentUser;
    }
    List<Language> getAllLanguages(){
        Spliterator<Language> spliterator = languageRepository.findAll().spliterator();
        return StreamSupport.stream(spliterator, false)
                .collect(Collectors.toList());
    }
    @Transactional
    public void addWord(WordDto wordDto) {
        User user = currentUser.getCurrentUser().orElseThrow(UserNotFoundException::new);
        user.setNewWords(user.getNewWords() + 1);
            user.setNewWordsWeek(user.getNewWordsWeek() + 1);
            List<Category> categoryList = categoryRepository.findCategoriesByUser_Email(user.getEmail()).stream()
                    .filter(category1 -> category1.getName().equalsIgnoreCase(wordDto.getCategoryName()))
                    .toList();
            Category category = null;
            if (!categoryList.isEmpty()) {
                category = categoryList.get(0);
            }
            if (categoryList.isEmpty()) {
                Category newCategory = new Category();
                newCategory.setName(wordDto.getCategoryName());
                newCategory.setUser(user);
                categoryRepository.save(newCategory);
                category = newCategory;
            }
            Language language = languageRepository.findByName(wordDto.getLanguageName()).orElseThrow();
            Word word = new Word(
                    wordDto.getPolishName(),
                    wordDto.getTranslation(),
                    wordDto.getImageLink(),
                    wordDto.getSentence(),
                    category,
                    language,
                    user);
            wordRepository.save(word);

    }

    public BeanPropertyBindingResult wordValidate(WordDto wordDto) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(wordDto, "wordDto");
        validator.validate(wordDto, errors);
        return errors;
    }

    @Transactional
    public void addWordsList(MultipartFile multipartFile, String language) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();
        byte[] bytes = inputStream.readAllBytes();
        String file = new String(bytes);
        addWordsFromText(file, language);
    }


    boolean isFileFormatValid(MultipartFile multipartFile){
       String contentType = multipartFile.getContentType();
       return Objects.equals(contentType, "text/plain");
    }

    boolean checkIsTextFileValid(String textLine){
        String regex = ".*?;.*?;.*?;.*?;.*?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textLine);
        return  matcher.matches();

    }
    @Transactional
    public void addWordsFromText(String text, String language) {
        if(text.isEmpty()){
            throw new RuntimeException("Nie podano żadnego tekstu.");
        }
        Scanner scanner = new Scanner(text);
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            String[] split = nextLine.split(";");
            boolean isTextLineValid = checkIsTextFileValid(nextLine);
            if(!isTextLineValid){
                throw new RuntimeException("Niepoprawny format tekstu.");
            }
            WordDto wordDto = new WordDto(split[0], split[1], split[2], split[3], split[4], language);
            List<String> errors = wordValidate(wordDto).getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            if(!errors.isEmpty()){
                throw new ValidationException(errors.toString());
            } else {
                addWord(wordDto);
            }
        }
    }


}
