package pl.languagelearn.application.category;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> findAll(){
        Iterable<Category> categories = categoryRepository.findAll();
        return StreamSupport.stream(categories.spliterator(), false)
                .map(CategoryMapper::map)
                .collect(Collectors.toList());
    }

    public Optional<CategoryDto> findCategoryById(long id){
        return categoryRepository.findById(id)
                .map(CategoryMapper::map);
    }
    public List<CategoryDto> findAllUserCategories(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return categoryRepository.findCategoriesByUser_Email(userDetails.getUsername()).stream()
                .map(CategoryMapper::map)
                .collect(Collectors.toList());

    }
    public List<CategoryDto> findAllUserCategoriesByLanguage(String language){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return categoryRepository.findCategoriesByUser_Email(userDetails.getUsername()).stream()
                .filter(category -> category.getWords().stream().anyMatch(word -> word.getLanguage().getName().equals(language)))
                .filter(category -> !category.getWords().isEmpty())
                .map(CategoryMapper::map)
                .collect(Collectors.toList());
    }

    public int getCategoryWordsByLanguage(CategoryDto category, String language){
        return (int) category.getWordDtos().stream()
                .filter(word -> word.getLanguageName().equals(language))
                .count();
    }

}

