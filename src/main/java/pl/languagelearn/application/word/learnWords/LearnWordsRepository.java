package pl.languagelearn.application.word.learnWords;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import pl.languagelearn.application.answer.Answer;

import java.util.*;
@Repository
class LearnWordsRepository {

    @Bean
    public Set<Answer> myObjects() {
        return new TreeSet<>();
    }
}