package pl.languagelearn.application.language;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LanguageRepository extends CrudRepository<Language, Long> {
    Optional<Language> findByName(String name);
}
