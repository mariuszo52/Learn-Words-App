package pl.languagelearn.application.language;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

   public List<LanguageDto> getAllLanguages(){
       return StreamSupport.stream(languageRepository.findAll().spliterator(), false)
                .map(LanguageMapper::map)
                .collect(Collectors.toList());
    }



}
