package pl.languagelearn.application.word;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import pl.languagelearn.application.language.Language;
import pl.languagelearn.application.category.Category;
import pl.languagelearn.application.user.User;

import java.time.LocalDateTime;

@Entity
public
class Word {
    public Word() {}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 2)
    @NotBlank
    private String polishName;
    @NotNull
    @Size(min = 2)
    @NotBlank
    private String translation;
    @NotNull
    @URL(message = "Niepoprawny format adresu URL")
    private String imageLink;
    @NotNull
    @Size(min = 2)
    @NotBlank
    private String sentence;
    @PastOrPresent
    private LocalDateTime lastRepeat;
    @Min(value = 1)
    @Max(value = 5)
    private Integer priority;
    @Min(value = 0)
    private Long repeatCounter;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Word(String polishName) {
        this.polishName = polishName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLastRepeat(LocalDateTime lastRepeat) {
        this.lastRepeat = lastRepeat;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getRepeatCounter() {
        return repeatCounter;
    }

    public void setRepeatCounter(Long repeatCounter) {
        this.repeatCounter = repeatCounter;
    }

    public Word(
                String polishName,
                String translation,
                String imageLink,
                String sentence,
                Category category,
                Language language,
                User user) {
        this.polishName = polishName;
        this.translation = translation;
        this.imageLink = imageLink;
        this.sentence = sentence;
        this.category = category;
        this.priority = 1;
        this.repeatCounter = 0L;
        this.language = language;
        this.user = user;
    }


    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getLastRepeat() {
        return lastRepeat;
    }

    public void setLastRepeat() {
        this.lastRepeat = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolishName() {
        return polishName;
    }

    public void setPolishName(String polishName) {
        this.polishName = polishName;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }



}
