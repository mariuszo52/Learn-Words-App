package pl.languagelearn.application.language;

import java.util.HashSet;
import java.util.Set;

public class LanguageDto {
    private long id;
    private String name;
    Set<String> wordNames = new HashSet<>();

    public Set<String> getWordNames() {
        return wordNames;
    }

    public void setWordNames(Set<String> wordNames) {
        this.wordNames = wordNames;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
