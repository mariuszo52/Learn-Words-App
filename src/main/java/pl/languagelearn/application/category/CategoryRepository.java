package pl.languagelearn.application.category;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    List<Category> findCategoriesByUser_Email(String username);
    Optional<Category> findByNameAndUser_Id(String name, long userId);




}