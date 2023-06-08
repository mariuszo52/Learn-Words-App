package pl.languagelearn.application.user;

import jakarta.mail.MessagingException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.languagelearn.application.email.EmailService;
import pl.languagelearn.application.exception.UserNotFoundException;
import pl.languagelearn.application.userRole.UserRole;
import pl.languagelearn.application.userRole.UserRoleRepository;
import pl.languagelearn.application.word.WordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final EmailService emailService;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String PASSWORD_CHARS = "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm!@#$%^&*(){}:<>,.";

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    UserService(EmailService emailService, WordRepository wordRepository,
                UserRepository userRepository, UserRoleRepository userRoleRepository,
                PasswordEncoder passwordEncoder) {
        this.emailService= emailService;
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(UserMapper::map);
    }
    public Optional<UserStatsDto> findUserStatsByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(UserMapper::mapToUserStatsDto);
    }
    private User prepareUserToSave(String username, String password, String userToken) {
        userRepository.findUserByEmail(username)
                .ifPresent(user -> {
                    throw new RuntimeException("Użytkownik o adresie " + user.getEmail() + " już istnieje.");
                });
        String encoded = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(username);
        user.setPassword(encoded);
        user.setConfirmationToken(userToken);
        userRoleRepository.findUserRoleByName(UserDto.USER_ROLE)
                .ifPresentOrElse(userRole -> setDefaultUserStats(user, userRole),
                        () -> { throw new  RuntimeException("Nie znaleziono roli użytkownika");
                        });
        return user;

    }

    @Transactional
    public void register(String username, String password) throws MessagingException {
        String userToken = emailService.generateRegistrationToken();
        User user = prepareUserToSave(username, password, userToken);
        userRepository.save(user);
        emailService.sendConfirmationEmail(username, userToken);
    }
        @Transactional
        public List<UserDto> findAllUsers(){
        Spliterator<User> usersSpliterator = userRepository.findAll().spliterator();
        return StreamSupport.stream(usersSpliterator, false)
                .map(UserMapper::map)
                .collect(Collectors.toList());
    }
        @Transactional
        public void deleteUser(long userId){ userRepository.deleteById(userId);
        }
        @Transactional
        public void updateUser(UserEditDto userEditDto){
            User user = userRepository.findUserById(userEditDto.getId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
            String encodedPass = passwordEncoder.encode(userEditDto.getPassword());
            user.setPassword(encodedPass);
            UserRole userRole = userRoleRepository.findUserRoleByName(userEditDto.getRole())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono roli użytkownika"));
            user.setUserRole(userRole);
            userRepository.save(user);
        }
        @Transactional
        public void deleteAllUserWords(){
        wordRepository.deleteAllByUser_id(getLoggedUserId());
        }
        @Transactional
        public void  resetStats(){
            User user = userRepository.findUserById(getLoggedUserId()).orElseThrow();
            user.setRepeatedWords(0);
            user.setRepeatedWordsToday(0);
            user.setAllTime(0);
            user.setTimeToday(0);
            user.setLearnedWords(0);
            user.setDaysInARow(0);
        }
        @Transactional
        public void  changePassword(String oldPass, String newPass){
            User user = userRepository.findUserById(getLoggedUserId()).orElseThrow(UserNotFoundException::new);
            String correctOldPass = user.getPassword();
            if (passwordEncoder.matches(oldPass, correctOldPass)){
                String encodedPass = passwordEncoder.encode(newPass);
                user.setPassword(encodedPass);
            }
            else{
                throw new RuntimeException("Wprowadzone hasło nie pasuje do starego hasła.");
            }
        }


    private void setDefaultUserStats(User user, UserRole userRole) {
        user.setUserRole(userRole);
        user.setLastLogin(DEFAULT_DATE);
        user.setRegisterDate(LocalDateTime.now());
    }
   public static Long getLoggedUserId(){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @Transactional
    public void blockUser(long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.setAccountNotLocked(false);
    }
        @Transactional
        public void unblockUser(long userId){
        User user = userRepository.findUserById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.setAccountNotLocked(true);
    }
        @Transactional
        public void confirmRegistration(String token){
        User user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Nie udało się potwierdzić rejestracji."));
        user.setAccountNotLocked(true);
    }
    @Transactional
    public void resetPassword(String email) throws MessagingException {
        String newPass = generatePassword();
        emailService.sendEmail("Zmiana hasła", email, "Twoje nowe hasło to: " + newPass);
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        user.setPassword(passwordEncoder.encode(newPass));
    }
     String generatePassword(){
        Matcher matcher;
        StringBuilder stringBuilder;
        do {
            stringBuilder = new StringBuilder();
            Random random = new Random();
            for (int i=0 ; i<9; i++){
                stringBuilder.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
            }
            Pattern pattern = Pattern.compile(PASSWORD_REGEX);
            matcher = pattern.matcher(stringBuilder.toString());
        }while (!matcher.matches());
        return stringBuilder.toString();
    }
}
