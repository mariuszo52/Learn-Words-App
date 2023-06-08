package pl.languagelearn.application.word;

import org.springframework.stereotype.Component;
import pl.languagelearn.application.word.dto.WordEditDto;
import pl.languagelearn.application.word.dto.WordDto;

@Component
public class WordMapper {
    public static WordEditDto mapToWordEditDto(Word word){
        WordEditDto wordEditDto = new WordEditDto();
        wordEditDto.setPolishName(word.getPolishName());
        wordEditDto.setTranslation(word.getTranslation());
        wordEditDto.setSentence(word.getSentence());
        wordEditDto.setImageLink(word.getImageLink());
        wordEditDto.setCategoryName(word.getCategory().getName());
        wordEditDto.setLanguageName(word.getLanguage().getName());
        return wordEditDto;
    }

    public static WordDto map(Word word){
        return new WordDto(
                word.getId(),
                word.getPolishName(),
                word.getTranslation(),
                word.getImageLink(),
                word.getSentence(),
                word.getLastRepeat(),
                word.getCategory().getName(),
                word.getLanguage().getId(),
                word.getUser().getId(),
                word.getLanguage().getName());

    }



}
