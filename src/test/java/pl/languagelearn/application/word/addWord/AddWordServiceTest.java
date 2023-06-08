package pl.languagelearn.application.word.addWord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.category.CategoryRepository;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.language.LanguageRepository;
import pl.languagelearn.application.user.CurrentUser;
import pl.languagelearn.application.user.User;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordRepository;
import pl.languagelearn.application.word.dto.WordDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AddWordServiceTest {
    @Mock
    WordRepository wordRepository;

    @Mock
    CategoryRepository categoryRepository;
    @Mock
    LanguageRepository languageRepository;
    @Mock
    Validator validator;
    @Mock
    CurrentUser currentUser;
    private WordDto wordDto;
    private Language language;

    private AddWordService addWordService;
    private User user;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        addWordService = new AddWordService(wordRepository, categoryRepository, languageRepository, validator, currentUser);
        wordDto = new WordDto("word1", "translation", "link",
                "sentence", "category1", "language1");
        Category category1 = new Category(1L, "category1");
        language = new Language();
        user = new User(1L, "user@example.com");
        user.setCategory(List.of(category1));
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }
    @Test
    void shouldReturn2WhenFindAllLanguages() {
        //given
        Language language1 = new Language(1L, "language1", Collections.emptyList());
        Language language2 = new Language(2L, "language2", Collections.emptyList());
        List<Language> allLanguages = List.of(language1, language2);
        when(languageRepository.findAll()).thenReturn(allLanguages);
        //when
        List<Language> result = addWordService.getAllLanguages();
        //then
        assertThat(result.size()).isEqualTo(allLanguages.size());
    }

    @Test
    void shouldReturnEmptyListWhenHaveNoLanguages() {
        //given
        when(languageRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<Language> result = addWordService.getAllLanguages();
        //then
        assertThat(result).isEmpty();
    }

    @Test
    void methodShouldBeCalledOnce() {
        //given
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        when(categoryRepository.findCategoriesByUser_Email(user.getEmail())).thenReturn(user.getCategory());
        when(languageRepository.findByName(wordDto.getLanguageName())).thenReturn(Optional.of(language));
        ArgumentCaptor<Word> wordArgumentCaptor = ArgumentCaptor.forClass(Word.class);
        //when
        addWordService.addWord(wordDto);
        //then
        verify(wordRepository, times(1)).save(wordArgumentCaptor.capture());
    }
    @Test
    void newWordsShouldBeEqual1(){
        //given
        user.setNewWords(0);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        when(categoryRepository.findCategoriesByUser_Email(user.getEmail())).thenReturn(user.getCategory());
        when(languageRepository.findByName(wordDto.getLanguageName())).thenReturn(Optional.of(language));
        //when
        addWordService.addWord(wordDto);
        //then
        assertThat(user.getNewWords()).isEqualTo(1);
    }
    @Test
    void shouldReturnTrueWhenFileFormatIsValid(){
        //given
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("text/plain");
        //when
        boolean result = addWordService.isFileFormatValid(multipartFile);
        //then
        assertThat(result).isTrue();
    }
    @Test
    void shouldReturnFalseWhenFileFormatIsValid(){
        //given
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("html");
        //when
        boolean result = addWordService.isFileFormatValid(multipartFile);
        //then
        assertThat(result).isFalse();
    }
    @Test
    void shouldThrowRuntimeExceptionWhenTextIsEmpty(){
        //given
        String text = "";
        //when //then
        assertThatThrownBy(() -> addWordService.addWordsFromText(text, "language")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> addWordService.addWordsFromText(text, "language")).hasMessage("Nie podano żadnego tekstu.");
    }
    @Test
    void shouldThrowRuntimeExceptionWhenTextLineIsNotValid() {
        //given
        String text = "textline";
        String textLine = "textline";
        AddWordService mock = mock(AddWordService.class);
        when(mock.checkIsTextFileValid(textLine)).thenReturn(false);
        //when //then
        assertThatThrownBy(() -> addWordService.addWordsFromText(text, "language")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> addWordService.addWordsFromText(text, "language")).hasMessage("Niepoprawny format tekstu.");

    }
    @Test
    void shouldCallOnceSaveWordMethod() {
        //given
        String text = "góra;mountain;https://example.com/mountain.jpg;W weekend wspinamy się na góry.;natura";
        String textLine = "góra;mountain;https://example.com/mountain.jpg;W weekend wspinamy się na góry.;natura";
        AddWordService addWordServiceSpy = Mockito.spy(addWordService);
        doNothing().when(addWordServiceSpy).addWord(any());
        when(addWordServiceSpy.checkIsTextFileValid(textLine)).thenReturn(true);
        //when
        addWordServiceSpy.addWordsFromText(text, "language");
        //then
        verify(addWordServiceSpy).addWord(any(WordDto.class));
    }
    }