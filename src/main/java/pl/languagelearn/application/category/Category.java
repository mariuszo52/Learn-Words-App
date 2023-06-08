package pl.languagelearn.application.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.languagelearn.application.user.User;
import pl.languagelearn.application.word.Word;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 2)
    @NotBlank
    private String name;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category")
    private List<Word> words = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Category(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Category() {
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
