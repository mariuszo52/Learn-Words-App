package pl.languagelearn.application.word.learnWords;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.languagelearn.application.answer.Answer;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.exception.WordNotFoundException;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.user.User;
import pl.languagelearn.application.user.UserService;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordRepository;
import pl.languagelearn.application.word.dto.WordDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class LearnWordsServiceTest {
    @Mock
    private WordRepository wordRepository;
    private LearnWordsService learnWordsService;
    private static MockedStatic<UserService> userServiceMockedStatic;
    private AutoCloseable autoCloseable;
    private Word word;
    private Word word2;

    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        learnWordsService = new LearnWordsService(wordRepository);
        User user = new User(1L, "email@gmail.com");
        Category category = new Category(1L, "category");
        Language language = new Language(1L, "language");
        word = new Word();
        word.setId(1L);
        word.setPolishName("word1");
        word.setTranslation("slowo1");
        word.setImageLink("link");
        word.setSentence("sentence");
        word.setLastRepeat(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        word.setCategory(category);
        word.setLanguage(language);
        word.setUser(user);
        word.setPriority(1);
        word.setRepeatCounter(1L);
        word2 = new Word();
        word2.setId(2L);
        word2.setPolishName("word2");
        word2.setTranslation("slowo2");
        word2.setImageLink("link2");
        word2.setSentence("sentence2");
        word2.setLastRepeat(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        word2.setCategory(category);
        word2.setLanguage(language);
        word2.setUser(user);
        word2.setPriority(1);
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }
    @BeforeAll
    static void init(){
        userServiceMockedStatic = mockStatic(UserService.class);
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
    }
    @AfterAll
    static void tearDown(){
        userServiceMockedStatic.close();

    }
    @Test
    void shouldReturnListWith2Words(){
        //given
        String language = "language";
        List<Word> words = List.of(word, word2);
        int maxPriority = 1;
       when(wordRepository.findWordsByPriorityAndUser_Id(maxPriority, 1L)).thenReturn(words);
       //when
        List<WordDto> result = learnWordsService.createWordsToRepeat(language);
        //then
        assertThat(result.size()).isEqualTo(2);
    }
    @Test
    void wordsLanguageAndPriorityShouldBeCorrect(){
        //given
        String language = "language";
        List<Word> words = List.of(word, word2);
        int maxPriority = 1;
        when(wordRepository.findWordsByPriorityAndUser_Id(maxPriority, 1L)).thenReturn(words);
        //when
        List<WordDto> result = learnWordsService.createWordsToRepeat(language);
        //then
        assertThat(result.get(0).getLanguageName()).isEqualTo(language);
        assertThat(result.get(0).getPriority()).isEqualTo(maxPriority);
        assertThat(result.get(1).getPriority()).isEqualTo(maxPriority);
        assertThat(result.get(1).getLanguageName()).isEqualTo(language);
    }
    @Test
    void shouldThrowWordNotFoundException(){
        //given
        when(wordRepository.findById(word.getId())).thenReturn(Optional.empty());
        //when//then
        assertThatThrownBy(() -> learnWordsService.updatePriority(word.getId()))
                .isInstanceOf(WordNotFoundException.class);
        assertThatThrownBy(() -> learnWordsService.updatePriority(word.getId()))
                .hasMessage("Nie znaleziono słowa o podanym id.");
    }
    @Test
    void shouldUpdatePriorityTo2(){
        //given
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        //when
        learnWordsService.updatePriority(1L);
        //then
        assertThat(word.getPriority()).isEqualTo(2);
    }
    @Test
    void updatedLastRepeatDateShouldNotBeEqualToPrevious(){
        //given
        LocalDateTime previousLastRepeat = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        //when
        learnWordsService.updatePriority(1L);
        //then
        assertThat(word.getLastRepeat())
                .isNotEqualTo(previousLastRepeat);
    }
    @Test
    void repeatCounterShouldEqualTo2(){
        //given
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        //when
        learnWordsService.updatePriority(1L);
        //then
        assertThat(word.getRepeatCounter()).isEqualTo(2);
    }
    @Test
    void repeatCounterShouldEqualTo1(){
        //given
        word.setRepeatCounter(null);
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        //when
        learnWordsService.updatePriority(1L);
        //then
        assertThat(word.getRepeatCounter()).isEqualTo(1);
    }
    @Test
    void repeatCounterShouldEqual2(){
        //given
        when(wordRepository.findById(word.getId())).thenReturn(Optional.of(word));
        //when
        learnWordsService.updatePriority(1L);
        //then
        assertThat(word.getRepeatCounter()).isEqualTo(2);
    }
    @Test
    void shouldReturnAnswerWithDefaultText(){
        //given
        String answer = "";
        String defaultAnswer = "Brak odpowiedzi";
        //when
        String result = learnWordsService.setDefaultAnswer(answer);
        //then
        assertThat(result).isEqualTo(defaultAnswer);
    }
    @Test
    void shouldReturnAnswerWithNoChanges(){
        //given
        String answer = "answer";
        //when
        String result = learnWordsService.setDefaultAnswer(answer);
        //then
        assertThat(result).isEqualTo(answer);
    }
    @Test
    void shouldReturnListWith2Elements(){
        //given
        long categoryId = 1L;
        String language = "language";
        List<Word> words = List.of(word, word2);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        //when
        List<Long> result = learnWordsService.getWordsByCategoryAndLanguage(language, categoryId);
        //then
        assertThat(result.size()).isEqualTo(2);
    }
    @Test
    void shouldReturnListWith1Element(){
        //given
        long categoryId = 1L;
        String language = "language";
        word2.setCategory(new Category(2L, "category2"));
        List<Word> words = List.of(word, word2);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        //when
        List<Long> result = learnWordsService.getWordsByCategoryAndLanguage(language, categoryId);
        //then
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    void shouldReturnEmptyListWhenUserHasNoWordsWithThatLanguage(){
        //given
        long categoryId = 1L;
        String language1Name = "language";
        Language language2 = new Language(2L, "language2");
        word.setLanguage(language2);
        word2.setLanguage(language2);
        List<Word> words = List.of(word, word2);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        //when
        List<Long> result = learnWordsService.getWordsByCategoryAndLanguage(language1Name, categoryId);
        //then
        assertThat(result).isEmpty();
    }
    @Test
    void shouldReturn2(){
        //given
        String language = "language";
        List<Word> words = List.of(word, word2);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        //when
        long result = learnWordsService.getNumberWordsToRepeatToday(language);
        //then
        assertThat(result).isEqualTo(2);
    }
    @Test
    void shouldReturn1(){
        //given
        String language = "language";
        word.setPriority(2);
        List<Word> words = List.of(word, word2);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(words);
        //when
        long result = learnWordsService.getNumberWordsToRepeatToday(language);
        //then
        assertThat(result).isEqualTo(1);
    }
    @Test
    void shouldReturn0(){
        //given
        String language = "language";
        when(wordRepository.findAllByUser_id(1L)).thenReturn(Collections.emptyList());
        //when
        long result = learnWordsService.getNumberWordsToRepeatToday(language);
        //then
        assertThat(result).isEqualTo(0);
    }

    @Test
    void shouldReturnTrueWhenAnswerIsCorrect(){
        //given
        WordDto wordDto = new WordDto();
        wordDto.setTranslation("answer");
        String answer = "answer";
        //when
        boolean result = learnWordsService.checkAnswer(wordDto, answer);
        //then
        assertThat(result).isTrue();
    }
    @Test
    void shouldReturnFalseWhenAnswerIsNotCorrect(){
        //given
        WordDto wordDto = new WordDto();
        wordDto.setTranslation("answer");
        String answer = "wrongAnswer";
        //when
        boolean result = learnWordsService.checkAnswer(wordDto, answer);
        //then
        assertThat(result).isFalse();
    }
    @Test
    void shouldReturnSetWith3Elements(){
        //given
        Answer answer = new Answer("answer1", "usertranslation1", true, "correcttranslation");
        Answer answer2 = new Answer("answer2", "usertranslation2", false, "correcttranslation2");
        Answer answer3 = new Answer("answer3", "usertranslation3", true, "correcttranslation3");
        Answer answer4 = new Answer("answer4", "usertranslation4", false, "correcttranslation4");
        Answer answer5 = new Answer("answer5", "usertranslation5", true, "correcttranslation5");
        Set<Answer> answers = Set.of(answer, answer2, answer3, answer4, answer5);
        //when
        Set<Answer> result = learnWordsService.findGoodAnswers(answers);
        //then
        assertThat(result.size()).isEqualTo(3);
    }
    @Test
    void shouldReturnSetWith1Element(){
        //given
        Answer answer = new Answer("answer1", "usertranslation1", false, "correcttranslation");
        Answer answer2 = new Answer("answer2", "usertranslation2", false, "correcttranslation2");
        Answer answer3 = new Answer("answer3", "usertranslation3", false, "correcttranslation3");
        Answer answer4 = new Answer("answer4", "usertranslation4", false, "correcttranslation4");
        Answer answer5 = new Answer("answer5", "usertranslation5", true, "correcttranslation5");
        Set<Answer> answers = Set.of(answer, answer2, answer3, answer4, answer5);
        //when
        Set<Answer> result = learnWordsService.findGoodAnswers(answers);
        //then
        assertThat(result.size()).isEqualTo(1);
    }
    double calculateProgressOfRepetition(int wordNumber, int numbersWordsToRepeat){
        return (double) wordNumber / numbersWordsToRepeat * 100;

    }
    @Test
    void shouldReturn20(){
        //given
        int wordNumber = 2;
        int numberWordsToRepeat = 10;
        //when
        double result = learnWordsService.calculateProgressOfRepetition(wordNumber, numberWordsToRepeat);
        //then
        assertThat(result).isEqualTo(20);
    }
    @Test
    void shouldReturn10(){
        //given
        int wordNumber = 1;
        int numberWordsToRepeat = 10;
        //when
        double result = learnWordsService.calculateProgressOfRepetition(wordNumber, numberWordsToRepeat);
        //then
        assertThat(result).isEqualTo(10);
    }
    @Test
    void shouldReturn100(){
        //given
        int wordNumber = 10;
        int numberWordsToRepeat = 10;
        //when
        double result = learnWordsService.calculateProgressOfRepetition(wordNumber, numberWordsToRepeat);
        //then
        assertThat(result).isEqualTo(100);
    }




}