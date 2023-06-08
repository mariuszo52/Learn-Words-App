package pl.languagelearn.application.category;

import pl.languagelearn.application.word.WordMapper;
import pl.languagelearn.application.word.dto.WordDto;

import java.util.Set;
import java.util.stream.Collectors;

public class CategoryMapper {

   public static CategoryDto map(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setNumberOfWords(category.getWords().size());
        Set<WordDto> wordDtoSet = category.getWords().stream().map(WordMapper::map).collect(Collectors.toSet());
        categoryDto.setWordDtos(wordDtoSet);
        return categoryDto;
    }
}
