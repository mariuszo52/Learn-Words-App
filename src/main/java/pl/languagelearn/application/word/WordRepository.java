package pl.languagelearn.application.word;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface WordRepository extends JpaRepository<Word, Long> {
        List<Word> findWordsByPriority(int priority);
        List<Word> findWordsByPriorityAndUser_Id(int priority, long userId);
        Page<Word> findAllByUser_idAndLanguage_Name(long userId, String languageName, Pageable pageable);
        Page<Word> findAllByUser_id(long userId, Pageable pageable);
        List<Word> findAllByUser_id(long userId);
        void deleteAllByUser_id(long userId);


}
