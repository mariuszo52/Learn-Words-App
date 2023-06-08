package pl.languagelearn.application.word.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class WordDto {
    public WordDto() {
    }

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
    @NotBlank
    @URL
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
    @NotNull
    @Size(min = 2)
    @NotBlank
    private String categoryName;
    private long languageId;
    private long userId;
    @NotNull
    @NotBlank
    @Size(min = 2)
    private String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setLastRepeat(LocalDateTime lastRepeat) {
        this.lastRepeat = lastRepeat;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getLastRepeat() {
        return lastRepeat;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setLastRepeat() {
        this.lastRepeat = LocalDateTime.now();
    }


    public WordDto(
            Long id,
            String polishName,
            String translation,
            String imageLink,
            String sentence,
            LocalDateTime lastRepeat,
            String categoryName,
            Long languageId,
            long userId,
            String languageName) {
        this.id = id;
        this.polishName = polishName;
        this.translation = translation;
        this.imageLink = imageLink;
        this.sentence = sentence;
        this.lastRepeat = lastRepeat;
        this.priority = 1;
        this.categoryName = categoryName;
        this.languageId = languageId;
        this.userId = userId;
        this.languageName = languageName;
    }

    public WordDto(String polishName, String translation, String imageLink, String sentence, String categoryName, String languageName) {
        this.polishName = polishName;
        this.translation = translation;
        this.imageLink = imageLink;
        this.sentence = sentence;
        this.categoryName = categoryName;
        this.languageName = languageName;
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

    @Override
    public String toString() {
        return "WordDto{" +
                "id=" + id +
                ", polishName='" + polishName + '\'' +
                ", translation='" + translation + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", sentence='" + sentence + '\'' +
                ", lastRepeat=" + lastRepeat +
                ", priority=" + priority +
                ", categoryName='" + categoryName + '\'' +
                ", languageId=" + languageId +
                ", userId=" + userId +
                ", languageName='" + languageName + '\'' +
                '}';
    }
}
