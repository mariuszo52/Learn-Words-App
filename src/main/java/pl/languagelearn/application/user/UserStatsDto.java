package pl.languagelearn.application.user;

import java.time.LocalDateTime;

public class UserStatsDto {
    private Long id;
    private int repeatedWords;
    private int repeatedWordsToday;
    private int daysInARow;
    private long allTime;
    private long timeToday;
    private long newWords;
    private long newWordsWeek;
    private int learnedWords;
    private int wordsToLearn;
    private LocalDateTime registerDate;
    private LocalDateTime lastLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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