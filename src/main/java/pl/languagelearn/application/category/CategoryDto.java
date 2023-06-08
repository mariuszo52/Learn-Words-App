package pl.languagelearn.application.category;

import pl.languagelearn.application.word.dto.WordDto;

import java.util.HashSet;
import java.util.Set;

public class CategoryDto{
    private Long id;
    private String name;

    public int getNumberOfWords() {
        return numberOfWords;
    }

    public void setNumberOfWords(int numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    private int numberOfWords;
    private int numbersOfWordsByLanguage;
    private Set<WordDto> wordDtos = new HashSet<>();

    public Set<WordDto> getWordDtos() {
        return wordDtos;
    }

    public void setWordDtos(Set<WordDto> wordDtos) {
        this.wordDtos = wordDtos;
    }

    public int getNumbersOfWordsByLanguage() {
        return numbersOfWordsByLanguage;
    }

    public void setNumbersOfWordsByLanguage(int numbersOfWordsByLanguage) {
        this.numbersOfWordsByLanguage = numbersOfWordsByLanguage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
