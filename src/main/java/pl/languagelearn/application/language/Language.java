package pl.languagelearn.application.language;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.languagelearn.application.word.Word;

import java.util.*;

@Entity
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    @Min(value = 2)
    private String name;
    @OneToMany(mappedBy = "language")
    private List<Word> words = new ArrayList<>();

    public Language(Long id, String name, List<Word> words) {
        this.id = id;
        this.name = name;
        this.words = words;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language() {
    }

    public Language(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
