package pl.languagelearn.application.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.email.EmailService;
import pl.languagelearn.application.userRole.UserRole;
import pl.languagelearn.application.validation.BcryptPassword;
import pl.languagelearn.application.word.Word;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Email
    private String email;
    @BcryptPassword
    private String password;
    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Category> category;
    @Min(value = 0)
    private int repeatedWords;
    @Min(value = 0)
    private int repeatedWordsToday;
    @Column(name = "days_in_a_row")
    @Min(value = 0)
    private int daysInARow;
    @Min(value = 0)
    private long allTime;
    @Min(value = 0)
    private long timeToday;
    @Min(value = 0)
    private long newWords;
    @Min(value = 0)
    private long newWordsWeek;
    @Min(value = 0)
    private int learnedWords;
    @Min(value = 0)
    private int wordsToLearn;
    @PastOrPresent
    private LocalDateTime registerDate;
    @PastOrPresent
    private LocalDateTime lastLogin;
    @NotNull
    private boolean isAccountNotLocked;
    @Size(min =  EmailService.TOKEN_SIZE)
    private String confirmationToken;
    public User() {
    }

    public User(Long id, String email, String password, UserRole userRole, boolean isAccountNotLocked, String confirmationToken) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.isAccountNotLocked = isAccountNotLocked;
        this.confirmationToken = confirmationToken;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public boolean isAccountNotLocked() {
        return isAccountNotLocked;
    }

    public void setAccountNotLocked(boolean accountNotLocked) {
        isAccountNotLocked = accountNotLocked;
    }

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Word> words = new ArrayList<>();

    public User(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public Long getId() {
        return id;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }


    public int getRepeatedWords() {
        return repeatedWords;
    }

    public void setRepeatedWords(int repeatedWords) {
        this.repeatedWords = repeatedWords;
    }

    public int getRepeatedWordsToday() {
        return repeatedWordsToday;
    }

    public void setRepeatedWordsToday(int repeatedWordsToday) {
        this.repeatedWordsToday = repeatedWordsToday;
    }

    public int getDaysInARow() {
        return daysInARow;
    }

    public void setDaysInARow(int daysInARow) {
        this.daysInARow = daysInARow;
    }

    public long getAllTime() {
        return allTime;
    }

    public void setAllTime(long allTime) {
        this.allTime = allTime;
    }

    public long getTimeToday() {
        return timeToday;
    }

    public void setTimeToday(long timeToday) {
        this.timeToday = timeToday;
    }

    public long getNewWords() {
        return newWords;
    }

    public void setNewWords(long newWords) {
        this.newWords = newWords;
    }

    public long getNewWordsWeek() {
        return newWordsWeek;
    }

    public void setNewWordsWeek(long newWordsWeek) {
        this.newWordsWeek = newWordsWeek;
    }

    public int getLearnedWords() {
        return learnedWords;
    }

    public void setLearnedWords(int learnedWords) {
        this.learnedWords = learnedWords;
    }

    public int getWordsToLearn() {
        return wordsToLearn;
    }

    public void setWordsToLearn(int wordsToLearn) {
        this.wordsToLearn = wordsToLearn;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

}

