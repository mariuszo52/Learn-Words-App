package pl.languagelearn.application.word;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.category.CategoryRepository;
import pl.languagelearn.application.exception.CannotEditException;
import pl.languagelearn.application.exception.CategoryNotFoundException;
import pl.languagelearn.application.exception.LanguageNotFoundException;
import pl.languagelearn.application.exception.WordNotFoundException;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.language.LanguageRepository;
import pl.languagelearn.application.user.User;
import pl.languagelearn.application.user.UserService;
import pl.languagelearn.application.word.dto.WordDto;
import pl.languagelearn.application.word.dto.WordEditDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class WordServiceTest {
    @Mock
    private WordRepository wordRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private LanguageRepository languageRepository;
    private WordService wordService;
    private Word word;
    private WordEditDto wordEditDto;
    private static MockedStatic<UserService> userServiceMockedStatic;
    private AutoCloseable autoCloseable;
    @BeforeAll
    static void init(){
        userServiceMockedStatic = mockStatic(UserService.class);
    }
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        wordService = new WordService(wordRepository, categoryRepository, languageRepository);
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
        wordEditDto = new WordEditDto();
        wordEditDto.setPolishName("wordEdit");
        wordEditDto.setTranslation("wordEditTranslation");
        wordEditDto.setImageLink("wordEditLink");
        wordEditDto.setSentence("wordEditSentence");
        wordEditDto.setCategoryName("wordEditCategory");
        wordEditDto.setLanguageName("wordEditLanguage");
        word = new Word();
        word.setId(1L);
        word.setPolishName("word1");
        word.setTranslation("slowo1");
        word.setImageLink("link");
        word.setSentence("sentence");
        word.setLastRepeat(LocalDateTime.now());
        word.setCategory(new Category(1L, "category"));
        word.setLanguage(new Language(1L, "language"));
        word.setUser(new User(1L, "email@gmail.com"));
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }
    @AfterAll
    static void tearDown(){
        userServiceMockedStatic.close();
    }
    @Test
    void shouldReturn2WhenFindAllWords() {
        //given
        List<Word> allWords = Collections.singletonList(word);
        when(wordRepository.findAll()).thenReturn(allWords);
        //when
        List<WordDto> result = wordService.findAllWords();
        //then
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    void shouldReturnEmptyListWhenFindAllWords(){
        //given
        List<Word> allWords = Collections.emptyList();
        when(wordRepository.findAll()).thenReturn(allWords);
        //when
        List<WordDto> result = wordService.findAllWords();
        //then
        assertThat(result).isEmpty();
    }
    @Test
    void shouldReturnCorrectWordWhenFindWordById(){
        //given
        long id = 1L;
        when(wordRepository.findById(id)).thenReturn(Optional.of(word));
        //when
        Optional<WordDto> wordDto = wordService.findWordById(id);
        //then
        assertThat(wordDto).isPresent();
        assertThat(wordDto.get().getId()).isEqualTo(1);
        Assertions.assertNotEquals(10, wordDto.get().getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserIdIsNotFound(){
        //given
        long id = 10L;
        when(wordRepository.findById(id)).thenReturn(Optional.empty());
        //when
        Optional<WordDto> wordDto = wordService.findWordById(id);
        //then
        assertThat(wordDto).isEmpty();
    }
    @Test
    void shouldReturnFalseWhenLinkIsInvalid(){
        //given
        String invalidLink = "www..invalid.com";
        //when
        boolean result = wordService.isImageLinkValid(invalidLink);
        //then
        assertThat(result).isFalse();
    }
    @Test
    void shouldReturnTrueWhenLinkIsValid(){
        //given
        String link = "https://google.com";
        //when
        boolean result = wordService.isImageLinkValid(link);
        //then
        assertThat(result).isTrue();
    }
    @Test
    void shouldReturn1WhenFindAllUserWords(){
        //given
        List<Word> allWords = Collections.singletonList(word);
        String language = "language";
        Pageable pageable = Pageable.ofSize(10);
        when(wordRepository.findAllByUser_idAndLanguage_Name(1L, language, pageable))
                .thenReturn(new PageImpl<>(allWords));
        //when
        Page<WordDto> result = wordService.getAllUserWords(pageable, language);
        //then
        assertThat(result.getSize()).isEqualTo(1);
    }
    @Test
    void shouldReturnEmptyPageWhenUserHaveNoWords(){
        //given
        long id = 1L;
        String language = "language";
        Pageable pageable = Pageable.ofSize(1);
        when(wordRepository.findAllByUser_idAndLanguage_Name(id, language, pageable))
                .thenReturn(Page.empty());
        //when
        Page<WordDto> result = wordService.getAllUserWords(pageable, language);
        //then
        assertThat(result).isEmpty();
    }
    @Test
    void shouldReturnEmptyPageWhenUserHaveNoWordsWithFoundName(){
        //given
        long id = 1L;
        String wordName = "word2";
        Pageable pageable = Pageable.ofSize(1);
        when(wordRepository.findAllByUser_id(id, pageable)).thenReturn(Page.empty());
        //when
        Page<WordDto> result = wordService.getFilteredUserWords(pageable,wordName);
        //then
        assertThat(result).isEmpty();
    }
    @Test
    void shouldReturn1WhenFindAllUserWordWithFoundName(){
        //given
        long id = 1L;
        List<Word> allWords = Collections.singletonList(word);
        String wordName = "word1";
        Pageable pageable = Pageable.ofSize(10);
        when(wordRepository.findAllByUser_id(id, pageable))
                .thenReturn(new PageImpl<>(allWords));
        //when
        Page<WordDto> result = wordService.getFilteredUserWords(pageable, wordName);
        //then
        assertThat(result.getSize()).isEqualTo(1);
    }
    @Test
    void shouldReturn1Language() {
        //given
        List<Word> allWords = Collections.singletonList(word);
        long id = 1L;
        when(wordRepository.findAllByUser_id(id)).thenReturn(allWords);
        //when
        Set<String> result = wordService.getUserLanguagesNames(id);
        //then
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    void shouldReturnEmptyListWhenUserHasNoAddedLanguages(){
        //given
        long id = 1L;
        when(wordRepository.findAllByUser_id(id)).thenReturn(Collections.emptyList());
        //when
        Set<String> result = wordService.getUserLanguagesNames(id);
        //then
        assertThat(result).isEmpty();
    }
    @Test
    void shouldThrowWordNotFoundException(){
        //given
        WordEditDto wordEditDto = new WordEditDto();
        when(wordRepository.findById(word.getId())).thenReturn(Optional.empty());
        //when//then
        assertThatThrownBy(() -> wordService.editWord(word.getId(),wordEditDto)).isInstanceOf(WordNotFoundException.class);
        assertThatThrownBy(() -> wordService.editWord(word.getId(),wordEditDto)).hasMessage("Nie znaleziono słowa o podanym id.");
    }
    @Test
    void shouldThrowCategoryNotFoundException(){
        //given
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        when(categoryRepository.findByNameAndUser_Id(wordEditDto.getCategoryName(), 1)).thenReturn(Optional.empty());
        //when//then
        assertThatThrownBy(() -> wordService.editWord(word.getId(),wordEditDto)).isInstanceOf(CategoryNotFoundException.class);
        assertThatThrownBy(() -> wordService.editWord(word.getId(),wordEditDto)).hasMessage("Nie znaleziono kategorii.");
    }
    @Test
    void shouldThrowLanguageNotFoundException(){
        //given
        Category category = new Category(1L, "wordEditCategory");
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        when(categoryRepository.findByNameAndUser_Id(wordEditDto.getCategoryName(), 1L)).thenReturn(Optional.of(category));
        when(languageRepository.findByName(wordEditDto.getLanguageName())).thenReturn(Optional.empty());
        //when//then
        assertThatThrownBy(() -> wordService.editWord(word.getId(),wordEditDto)).isInstanceOf(LanguageNotFoundException.class);
        assertThatThrownBy(() -> wordService.editWord(word.getId(),wordEditDto)).hasMessage("Nie znaleziono języka o podanej nazwie.");
    }
    @Test
    void wordFieldsShouldBeEqualWordEditDtoFields(){
        //given
        Language language = new Language(1L, wordEditDto.getLanguageName());
        Category category = new Category(1L, "wordEditCategory");
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        when(categoryRepository.findByNameAndUser_Id(wordEditDto.getCategoryName(), 1L)).thenReturn(Optional.of(category));
        when(languageRepository.findByName(wordEditDto.getLanguageName())).thenReturn(Optional.of(language));
        //when
        wordService.editWord(word.getId(), wordEditDto);
        //then
        assertThat(word.getPolishName()).isEqualTo(wordEditDto.getPolishName());
        assertThat(word.getTranslation()).isEqualTo(wordEditDto.getTranslation());
        assertThat(word.getImageLink()).isEqualTo(wordEditDto.getImageLink());
        assertThat(word.getSentence()).isEqualTo(wordEditDto.getSentence());
        assertThat(word.getCategory().getName()).isEqualTo(wordEditDto.getCategoryName());
        assertThat(word.getLanguage().getName()).isEqualTo(wordEditDto.getLanguageName());
    }

    @Test
    void shouldCallDeleteWordByIdMethodOnce(){
        //given
        List<Word> words = List.of(word);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        when(wordRepository.findById(1L)).thenReturn(Optional.of(words.get(0)));
        //when
        wordService.deleteWord(1L);
        //then
        verify(wordRepository, times(1)).deleteById(1L);
    }
    @Test
    void shouldThrowWordNotFoundExceptionWhenTryDeleteWord(){
        //given
        List<Word> words = List.of(word);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        when(wordRepository.findById(1L)).thenReturn(Optional.empty());
        //when//
        assertThatThrownBy(() -> wordService.deleteWord(2L)).isInstanceOf(WordNotFoundException.class);
        assertThatThrownBy(() -> wordService.deleteWord(2L)).hasMessage("Nie znaleziono słowa o podanym id.");
    }
    @Test
    void shouldThrowCannotEditExceptionWhenTryDeleteWord(){
        //given
        Word word2 = new Word("word2");
        word2.setId(2L);
        List<Word> words = List.of(word);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        when(wordRepository.findById(2L)).thenReturn(Optional.of(word2));
        //when//
        assertThatThrownBy(() -> wordService.deleteWord(2L)).isInstanceOf(CannotEditException.class);
        assertThatThrownBy(() -> wordService.deleteWord(2L)).hasMessage("Nie można usunąć słowa o podanym ID.");
    }


    }

