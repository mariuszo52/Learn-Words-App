package pl.languagelearn.application.language;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.languagelearn.application.word.Word;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageServiceTest {
    @Mock LanguageRepository languageRepository;
    private LanguageService languageService;
    private AutoCloseable autoCloseable;
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        languageService = new LanguageService(languageRepository);
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldReturn3languages(){
        //given
        List<Word> words = List.of(new Word("word1"), new Word("word2"));
        Language language1 = new Language(1L, "language1", words);
        Language language2 = new Language(2L, "language2", words);
        Language language3 = new Language(3L, "language3", words);
        List<Language> languages = List.of(language1, language2, language3);
        Mockito.when(languageRepository.findAll()).thenReturn(languages);

        //when
        List<LanguageDto> allLanguages = languageService.getAllLanguages();
        //given
        assertThat(allLanguages.size()).isEqualTo(3);
    }
    @Test
    void shouldReturnEmptyList(){
        //given
        Mockito.when(languageRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<LanguageDto> allLanguages = languageService.getAllLanguages();
        //given
        assertThat(allLanguages).isEmpty();
    }


}