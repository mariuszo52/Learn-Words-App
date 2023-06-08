package pl.languagelearn.application.user;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.languagelearn.application.exception.UserNotFoundException;
import pl.languagelearn.application.time.TimeProvider;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserStatsServiceTest {
    @Mock UserRepository userRepository;
    @Mock WordRepository wordRepository;
    @Mock CurrentUser currentUser;
    @Mock
    TimeProvider timeProvider;
    private UserStatsService userStatsService;
    private static MockedStatic<UserService> userServiceMockedStatic;
    private AutoCloseable autoCloseable;
    @BeforeAll
    static void init(){
        userServiceMockedStatic = mockStatic(UserService.class);
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
    }
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        userStatsService = new UserStatsService(userRepository, wordRepository, currentUser, timeProvider);
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
    void shouldIncreaseValues(){
        // given
        User user = new User();
        user.setRepeatedWords(5);
        user.setRepeatedWordsToday(3);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        // when
        userStatsService.updateRepeatedWordsCounter();
        // then
        assertEquals(6, user.getRepeatedWords());
        assertEquals(4, user.getRepeatedWordsToday());
    }
    @Test
    void shouldThrowUserNotFoundExceptionWhenCurrentUserIsNotFound(){
        //given
        when(currentUser.getCurrentUser()).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userStatsService.updateRepeatedWordsCounter()).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userStatsService.updateRepeatedWordsCounter()).hasMessage("Nie znaleziono użytkownika.");
    }


    @Test
    void shouldIncreaseValuesBy3600(){
        //given
        User user = new User(1L, "user@example.com");
        user.setLastLogin(LocalDateTime.of(2023, 1,1, 0, 0, 0));
        user.setAllTime(60L);
        user.setTimeToday(0L);
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        LocalDateTime logoutTime = LocalDateTime.of(2023, 1, 1, 1, 0, 0);
        //when
        userStatsService.updateLearningTime(logoutTime);
        //then
        assertEquals(user.getAllTime(), 3660);
        assertEquals(user.getTimeToday(), 3600);
    }
    @Test
    void shouldIncreaseValuesBy1(){
        //given
        User user = new User(1L, "user@example.com");
        user.setLastLogin(LocalDateTime.of(2023, 1,1, 0, 0, 0));
        user.setAllTime(0L);
        user.setTimeToday(0L);
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        LocalDateTime logoutTime = LocalDateTime.of(2023, 1, 1, 0, 0, 1);
        //when
        userStatsService.updateLearningTime(logoutTime);
        //then
        assertEquals(user.getAllTime(), 1);
        assertEquals(user.getTimeToday(), 1);
    }
    @Test
    void shouldReturnRuntimeExceptionWhenUserToUpdateIsNotFound(){
        //given
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());
        LocalDateTime logoutTime = LocalDateTime.of(2023, 1, 1, 0, 0, 1);
        //when//then
        assertThatThrownBy(() -> userStatsService.updateLearningTime(logoutTime)).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userStatsService.updateLearningTime(logoutTime)).hasMessage("Nie znaleziono użytkownika.");
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenCurrentUserToUpdateIsNotFound() {
        //given
        when(currentUser.getCurrentUser()).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userStatsService.updateDaysInARowStat()).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userStatsService.updateDaysInARowStat()).hasMessage("Nie znaleziono użytkownika.");
    }
    @Test
    void shouldEqual1WhenLastLoginWasBeforeNewDay(){
        User user = new User(1L, "user@gmail.com");
        user.setLastLogin(LocalDateTime.of(2023, 1, 1, 10, 0, 0 ));
        user.setDaysInARow(5);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2023, 1, 1, 12, 0, 0 ));
        //when
        userStatsService.updateDaysInARowStat();
        //then
        assertThat(user.getDaysInARow()).isEqualTo(1);
    }
    @Test
    void shouldEqual2WhenWasLogged2DaysInARow(){
        User user = new User(1L, "user@gmail.com");
        user.setLastLogin(LocalDateTime.of(2023, 1, 1, 10, 0, 0 ));
        user.setDaysInARow(1);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2023, 1, 2, 12, 0, 0 ));
        //when
        userStatsService.updateDaysInARowStat();
        //then
        assertThat(user.getDaysInARow()).isEqualTo(2);
    }
    @Test
    void shouldEqual1WhenWasMin1DayBreakOfLogging(){
        User user = new User(1L, "user@gmail.com");
        user.setLastLogin(LocalDateTime.of(2023, 1, 1, 10, 0, 0 ));
        user.setDaysInARow(2);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2023, 1, 3, 12, 0, 0 ));
        //when
        userStatsService.updateDaysInARowStat();
        //then
        assertThat(user.getDaysInARow()).isEqualTo(1);
    }
    @Test
    void shouldEqual3WhenWasLogged3DaysInARow(){
        User user = new User(1L, "user@gmail.com");
        user.setLastLogin(LocalDateTime.of(2023, 1, 1, 10, 0, 0 ));
        user.setDaysInARow(2);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2023, 1, 2, 23, 59, 59));
        //when
        userStatsService.updateDaysInARowStat();
        //then
        assertThat(user.getDaysInARow()).isEqualTo(3);
    }
    @Test
    void lastLoginShouldBeEqualToCurrentTime(){
        User user = new User(1L, "user@gmail.com");
        user.setLastLogin(LocalDateTime.of(2023, 1, 1, 10, 0, 0 ));
        user.setDaysInARow(2);
        when(currentUser.getCurrentUser()).thenReturn(Optional.of(user));
        LocalDateTime currenTime = LocalDateTime.of(2023, 1, 2, 23, 59, 59);
        when(timeProvider.getCurrentTime()).thenReturn(currenTime);
        //when
        userStatsService.updateDaysInARowStat();
        //then
        assertThat(user.getLastLogin()).isEqualTo(currenTime);
    }

    @Test
    void numberOfLearnedWordsShouldEqual2() {
        //given
        User user1 = new User(1L, "user@gmail.com");
        Word word1 = new Word("word1");
        word1.setUser(user1);
        word1.setPriority(5);
        Word word2 = new Word("word2");
        word2.setUser(user1);
        word2.setPriority(5);
        Word word3 = new Word("word3");
        word3.setUser(user1);
        word3.setPriority(4);
        List<Word> allUserWords = List.of(word1, word2, word3);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(allUserWords);
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user1));
        //when
        userStatsService.setLearnedWordsStats();
        //then
        assertThat(user1.getLearnedWords()).isEqualTo(2);
        assertThat(user1.getWordsToLearn()).isEqualTo(1);
    }
    @Test
    void numberOfLearnedWordsShouldEqual0() {
        //given
        User user1 = new User(1L, "user@gmail.com");
        Word word1 = new Word("word1");
        word1.setUser(user1);
        word1.setPriority(1);
        Word word2 = new Word("word2");
        word2.setUser(user1);
        word2.setPriority(1);
        Word word3 = new Word("word3");
        word3.setUser(user1);
        word3.setPriority(1);
        List<Word> allUserWords = List.of(word1, word2, word3);
        when(wordRepository.findAllByUser_id(1L)).thenReturn(allUserWords);
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user1));
        //when
        userStatsService.setLearnedWordsStats();
        //then
        assertThat(user1.getLearnedWords()).isEqualTo(0);
        assertThat(user1.getWordsToLearn()).isEqualTo(3);
    }
    @Test
    void shouldThrowRuntimeExceptionWhenLoggedUserIsNotFound(){
        //given
        when(userRepository.findUserById(5L)).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userStatsService.setLearnedWordsStats()).isInstanceOf(RuntimeException.class);

    }
}