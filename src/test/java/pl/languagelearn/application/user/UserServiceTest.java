package pl.languagelearn.application.user;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.languagelearn.application.email.EmailService;
import pl.languagelearn.application.exception.UserNotFoundException;
import pl.languagelearn.application.userRole.UserRole;
import pl.languagelearn.application.userRole.UserRoleRepository;
import pl.languagelearn.application.word.WordRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    WordRepository wordRepository;
    @Mock
    UserRoleRepository userRoleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    User user;
    @Mock
    EmailService emailService;
    private UserService userService;
    private static MockedStatic<UserService> userServiceMockedStatic;
    private AutoCloseable autoCloseable;

    @BeforeAll
    static void init() {
        userServiceMockedStatic = mockStatic(UserService.class);
    }

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(emailService, wordRepository, userRepository, userRoleRepository, passwordEncoder);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("user1@example.com");
        when(user.getUserRole()).thenReturn(new UserRole("ADMIN"));
        when(user.getPassword()).thenReturn("password");
        when(user.isAccountNotLocked()).thenReturn(true);
        when(user.getConfirmationToken()).thenReturn("ABCabc123456789");
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }

    @AfterAll
    static void tearDown() {
        userServiceMockedStatic.close();
    }

    @Test
    void shouldReturnUserWithCorrectFieldsByEmail() {
        //given
        when(userRepository.findUserByEmail("user1@example.com")).thenReturn(Optional.of(user));
        //when
        Optional<UserDto> resultUser = userService.findUserByEmail("user1@example.com");
        //then
        assertTrue(resultUser.isPresent());
        assertThat(resultUser.get())
                .extracting(UserDto::getEmail, UserDto::getId, UserDto::getPassword, UserDto::isAccountNotLocked, UserDto::getRole, UserDto::getConfirmationToken)
                .containsExactly("user1@example.com", 1L, "password", true, "ADMIN", "ABCabc123456789");
    }

    @Test
    void shouldReturnEmptyOptionalWhenEmailIsNotCorrect() {
        //given
        when(userRepository.findUserByEmail("wrongemail@example.com")).thenReturn(Optional.of(user));
        //when
        Optional<UserDto> resultUser = userService.findUserByEmail("user1@example.com");
        //then
        assertThat(resultUser).isEmpty();
    }

    @Test
    void shouldReturnCorrectUserStatsDto() {
        //given
        when(userRepository.findUserByEmail("user1@example.com")).thenReturn(Optional.of(user));
        //when
        Optional<UserStatsDto> resultUserStatsDto = userService.findUserStatsByEmail("user1@example.com");
        //
        assertThat(resultUserStatsDto).isPresent();
        assertThat(resultUserStatsDto.get().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyOptional() {
        //given
        when(userRepository.findUserByEmail("user1@example.com")).thenReturn(Optional.of(user));
        //when
        Optional<UserStatsDto> resultUserStatsDto = userService.findUserStatsByEmail("wrongemail@example.pl");
        //then
        assertThat(resultUserStatsDto).isEmpty();
    }

    @Test
    void shouldReturnUserWithCorrectUsernameAndPassword() throws MessagingException {
        //given
        when(userRepository.findUserByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        UserRole userRole = new UserRole("USER");
        when(userRoleRepository.findUserRoleByName("USER")).thenReturn(Optional.of(userRole));
        //when
        userService.register("user@gmail.com", "password");
        //then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User resultUser = argumentCaptor.getValue();
        assertThat(resultUser.getEmail()).isEqualTo("user@gmail.com");
        assertThat(resultUser.getPassword()).isEqualTo("encodedpassword");
        assertThat(resultUser.getUserRole().getName()).isEqualTo("USER");


    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserAlreadyExist() {
        //given
        User user = new User(1L, "user@gmail.com");
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        //when //then
        assertThatThrownBy(() -> userService.register(user.getEmail(), user.getPassword()))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userService.register(user.getEmail(), user.getPassword()))
                .hasMessage("Użytkownik o adresie " + user.getEmail() + " już istnieje.");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserRoleNotExist() {
        //given
        when(userRepository.findUserByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(userRoleRepository.findUserRoleByName("NEWROLE")).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.register("user@gmail.com", "password"))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userService.register("user@gmail.com", "password"))
                .hasMessage("Nie znaleziono roli użytkownika");

    }

    @Test
    void encodeMethodShouldBeCalled() throws MessagingException {
        when(userRepository.findUserByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        UserRole userRole = new UserRole("USER");
        when(userRoleRepository.findUserRoleByName("USER")).thenReturn(Optional.of(userRole));
        //when
        userService.register("user@gmail.com", "password");
        //then
        verify(passwordEncoder).encode("password");
    }

    @Test
    void sendConfirmationEmailMethodShouldBeCalled() throws MessagingException {
        when(userRepository.findUserByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(emailService.generateRegistrationToken()).thenReturn("registrationtoken");
        UserRole userRole = new UserRole("USER");
        when(userRoleRepository.findUserRoleByName("USER")).thenReturn(Optional.of(userRole));
        //when //then
        userService.register("user@gmail.com", "password");
        verify(emailService).sendConfirmationEmail("user@gmail.com", "registrationtoken");
    }

    @Test
    void sendConfirmationEmailMethodShouldNotBeCalledWithWrongEmail() throws MessagingException {
        when(userRepository.findUserByEmail("user@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(emailService.generateRegistrationToken()).thenReturn("registrationtoken");
        UserRole userRole = new UserRole("USER");
        when(userRoleRepository.findUserRoleByName("USER")).thenReturn(Optional.of(userRole));
        //when //then
        userService.register("user@gmail.com", "password");
        verify(emailService, times(0))
                .sendConfirmationEmail("wrong@gmail.com", "registrationtoken");
    }

    @Test
    void shouldReturn3UsersWhenDatabaseHave3Users() {
        //given
        UserRole userRole = new UserRole("USER");
        User user1 = new User(1L, "user1@gmail.com", "pass1", userRole,
                true, "token1");
        User user2 = new User(2L, "user2@gmail.com", "pass2", userRole,
                true, "token2");
        User user3 = new User(3L, "user3@gmail.com", "pass3", userRole,
                true, "token3");
        Iterable<User> allUsers = List.of(user1, user2, user3);
        when(userRepository.findAll()).thenReturn(allUsers);
        //when
        List<UserDto> result = userService.findAllUsers();
        //then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void shouldReturnEmptyListWhenDatabaseNoHaveRecords() {
        //given
        Iterable<User> allUsers = Collections.emptyList();
        when(userRepository.findAll()).thenReturn(allUsers);
        //when
        List<UserDto> result = userService.findAllUsers();
        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCallDeleteByIdMethod() {
        //given
        long userId = 125;
        doNothing().when(userRepository).deleteById(userId);
        //when
        userService.deleteUser(userId);
        //then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void shouldChangeUser() {
        //given
        UserEditDto userEditDto = new UserEditDto(1L, "user1@gmail.com", "USER");
        userEditDto.setPassword("password");
        User user1 = new User(1L, "oldemail@gmail.com");
        UserRole userRole = new UserRole("USER");
        when(userRepository.findUserById(userEditDto.getId())).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(userEditDto.getPassword())).thenReturn("encodedPassword");
        when(userRoleRepository.findUserRoleByName(userEditDto.getRole())).thenReturn(Optional.of(userRole));
        //when
        userService.updateUser(userEditDto);
        //then
        verify(userRepository).findUserById(user1.getId());
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User resultUser = argumentCaptor.getValue();
        assertThat(resultUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(resultUser.getUserRole().getName()).isEqualTo("USER");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserIsNotFound() {
        //given
        UserEditDto userEditDto = new UserEditDto(2L, "user1@gmail.com", "USER");
        when(userRepository.findUserById(userEditDto.getId())).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.updateUser(userEditDto)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userService.updateUser(userEditDto)).hasMessage("Nie znaleziono użytkownika");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserRoleIsNotFound() {
        //given
        UserEditDto userEditDto = new UserEditDto(1L, "user1@gmail.com", "USER");
        userEditDto.setPassword("password");
        User user1 = new User(1L, "oldemail@gmail.com");
        when(userRepository.findUserById(userEditDto.getId())).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(userEditDto.getPassword())).thenReturn("encodedPassword");
        when(userRoleRepository.findUserRoleByName(userEditDto.getRole())).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.updateUser(userEditDto)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userService.updateUser(userEditDto)).hasMessage("Nie znaleziono roli użytkownika");
    }

    @Test
    void shouldCallMethodOnce() {
        //given
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(125L);
        //when
        userService.deleteAllUserWords();
        //then
        verify(wordRepository, times(1)).deleteAllByUser_id(UserService.getLoggedUserId());
    }

    @Test
    void shouldResetAllStatsValuesTo0() {
        //given
        User user1 = new User(1L, "user@example.com");
        user1.setRepeatedWords(5000);
        user1.setRepeatedWordsToday(100);
        user1.setAllTime(100);
        user1.setTimeToday(2);
        user1.setLearnedWords(50);
        user1.setDaysInARow(10);
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
        when(userRepository.findUserById(UserService.getLoggedUserId())).thenReturn(Optional.of(user1));
        //when
        userService.resetStats();
        //then
        assertThat(user1)
                .extracting(User::getRepeatedWords, User::getRepeatedWordsToday, User::getAllTime,
                        User::getTimeToday, User::getLearnedWords, User::getDaysInARow)
                .containsExactly(0, 0, 0L, 0L, 0, 0);
    }

    @Test
    void shouldOldPasswordBeNotEqualNew() {
        //given
        User user1 = new User(1L, "user@example.com");
        user1.setPassword("password");
        String oldPass = "password";
        String newPass = "newPass";
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
        when(userRepository.findUserById(UserService.getLoggedUserId())).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(oldPass, user1.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPass)).thenReturn("encodedPass");
        //when
        userService.changePassword(oldPass, newPass);
        //then
        assertThat(user1.getPassword()).isNotEqualTo(oldPass);
        assertThat(user1.getPassword()).isEqualTo("encodedPass");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenLoggedUserIsNotFound() {
        //given
        String oldPass = "password";
        String newPass = "newPass";
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
        when(userRepository.findUserById(UserService.getLoggedUserId())).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.changePassword(oldPass, newPass)).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.changePassword(oldPass, newPass)).hasMessage("Nie znaleziono użytkownika.");
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenOldPassIsNotCorrect() {
        //given
        User user1 = new User(1L, "user@example.com");
        user1.setPassword("password");
        String oldPass = "wrongOldPass";
        String newPass = "newPass";
        userServiceMockedStatic.when(UserService::getLoggedUserId).thenReturn(1L);
        when(userRepository.findUserById(UserService.getLoggedUserId())).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(oldPass, user1.getPassword())).thenReturn(false);
        //when //then
        assertThatThrownBy(() -> userService.changePassword(oldPass, newPass))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userService.changePassword(oldPass, newPass))
                .hasMessage("Wprowadzone hasło nie pasuje do starego hasła.");
    }

    @Test
    void shouldReturnTrueWhenUserIsBlocked() {
        //given
        User user1 = new User(1L, "user@gmail.com");
        user1.setAccountNotLocked(true);
        when(userRepository.findUserById(user1.getId())).thenReturn(Optional.of(user1));
        //when
        userService.blockUser(user1.getId());
        //then
        assertThat(user1.isAccountNotLocked()).isFalse();

    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserToBlockIsNotFound() {
        //given
        when(userRepository.findUserById(2L)).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.blockUser(2L)).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.blockUser(2L)).hasMessage("Nie znaleziono użytkownika.");
    }

    @Test
    void shouldReturnTrueWhenUserIsNotBlocked() {
        //given
        User user1 = new User(1L, "user@gmail.com");
        user1.setAccountNotLocked(false);
        when(userRepository.findUserById(user1.getId())).thenReturn(Optional.of(user1));
        //when
        userService.unblockUser(user1.getId());
        //then
        assertThat(user1.isAccountNotLocked()).isTrue();

    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserToUnblockIsNotFound() {
        //given
        when(userRepository.findUserById(2L)).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.unblockUser(2L)).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.unblockUser(2L)).hasMessage("Nie znaleziono użytkownika.");
    }

    @Test
    void shouldReturnTrueWhenUserTokenIsFound() {
        //given
        User user1 = new User(1L, "user@gmail.com");
        user1.setAccountNotLocked(false);
        user1.setConfirmationToken("token");
        when(userRepository.findByConfirmationToken(user1.getConfirmationToken())).thenReturn(Optional.of(user1));
        //when
        userService.confirmRegistration("token");
        //then
        assertThat(user1.isAccountNotLocked()).isTrue();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserWithTokenIsNotFound() {
        //given
        when(userRepository.findByConfirmationToken("token")).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.confirmRegistration("token")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userService.confirmRegistration("token")).hasMessage("Nie udało się potwierdzić rejestracji.");
    }

    @Test
    void shouldReturnTrueWhenEmailWasSent() throws MessagingException {
        //given
        String subject = "Zmiana hasła";
        User user1 = new User(1L, "user@gmail.com");
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        //when
        userService.resetPassword(user1.getEmail());
        //then
        verify(emailService, times(1)).sendEmail(matches(subject), matches(user1.getEmail()), anyString());
    }

    @Test
    void shouldReturnTrueWhenPasswordHasChanged() throws MessagingException {
        //given
        User user1 = new User(1L, "user@gmail.com");
        user1.setPassword("oldpassword");
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        //when
        userService.resetPassword(user1.getEmail());
        //then
        assertThat(user1.getPassword()).isNotEqualTo("oldpassword");
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserIsNotFoundByEmail() {
        //given
        User user1 = new User(1L, "user@gmail.com");
        when(userRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.empty());
        //when //then
        assertThatThrownBy(() -> userService.resetPassword(user1.getEmail())).isInstanceOf(UserNotFoundException.class);
        assertThatThrownBy(() -> userService.resetPassword(user1.getEmail())).hasMessage("Nie znaleziono użytkownika.");
    }


}