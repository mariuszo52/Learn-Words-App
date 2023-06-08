package pl.languagelearn.application.language;

import pl.languagelearn.application.word.Word;

import java.util.Set;
import java.util.stream.Collectors;

class LanguageMapper {

    public static LanguageDto map (Language language){
        LanguageDto languageDto = new LanguageDto();
        languageDto.setId(language.getId());
        languageDto.setName(language.getName());
        Set<String> wordsIds = language.getWords().stream()
                .map(Word::getPolishName)
                .collect(Collectors.toSet());
        languageDto.setWordNames(wordsIds);
        return languageDto;
    }
}
