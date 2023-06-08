package pl.languagelearn.application.word.learnWords;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
class ChangePriorityScheduler {
    private final WordRepository wordRepository;

    ChangePriorityScheduler(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void changePriority(){
       changePriorityOfRepeat(2,24);
       changePriorityOfRepeat(3, 5 * 24);
       changePriorityOfRepeat(4, 20 * 24);
       changePriorityOfRepeat(5, 90 * 24);
    }
    void changePriorityOfRepeat(int priority, int timeOfPauseInHours){
        List<Word> wordsWithPriority = wordRepository.findWordsByPriority(priority);
        for (Word word : wordsWithPriority) {
            if (ChronoUnit.HOURS.between(word.getLastRepeat(), LocalDateTime.now()) > timeOfPauseInHours) {
                word.setPriority(1);
            }
            wordRepository.save(word);
        }



    }


    }

