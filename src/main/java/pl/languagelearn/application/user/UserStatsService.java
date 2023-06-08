package pl.languagelearn.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.languagelearn.application.exception.UserNotFoundException;
import pl.languagelearn.application.time.TimeProvider;
import pl.languagelearn.application.word.Word;
import pl.languagelearn.application.word.WordRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserStatsService {
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final CurrentUser currentUser;
    private final TimeProvider timeProvider;

    UserStatsService(UserRepository userRepository, WordRepository wordRepository, CurrentUser currentUser, TimeProvider timeProvider) {
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.currentUser = currentUser;
        this.timeProvider = timeProvider;
    }



    public void updateRepeatedWordsCounter(){
        User user = currentUser.getCurrentUser().orElseThrow(UserNotFoundException::new);
        user.setRepeatedWords(user.getRepeatedWords() + 1);
        user.setRepeatedWordsToday(user.getRepeatedWordsToday() + 1);
    }
    @Transactional
    public void updateDaysInARowStat(){
        User user = currentUser.getCurrentUser().orElseThrow(UserNotFoundException::new);
        LocalDateTime currentTime = timeProvider.getCurrentTime();
        LocalDateTime newDay  = user.getLastLogin().plus(1, ChronoUnit.DAYS).withHour(0).withMinute(0).withSecond(0);
        if (currentTime.isBefore(newDay)){
            user.setDaysInARow(1);
        }
        if (currentTime.isAfter(newDay) && currentTime.isBefore(newDay.plusDays(1))){
            user.setDaysInARow(user.getDaysInARow() + 1);
        }
        if(currentTime.isAfter(newDay.plusDays(1))){
            user.setDaysInARow(1);
        }
        user.setLastLogin(currentTime);
    }
    @Transactional
    public void updateLearningTime(LocalDateTime logoutTime){
        Long loggedUserId = UserService.getLoggedUserId();
        User user = userRepository.findUserById(loggedUserId)
                .orElseThrow(UserNotFoundException::new);
        LocalDateTime lastLogin = user.getLastLogin();
        long learningTime = ChronoUnit.SECONDS.between(lastLogin, logoutTime);
        user.setAllTime(user.getAllTime() + learningTime);
        user.setTimeToday(user.getTimeToday() + learningTime);
    }
    @Transactional
    public void setLearnedWordsStats(){
        Long loggedUserId = UserService.getLoggedUserId();
        User user = userRepository.findUserById(loggedUserId).orElseThrow(UserNotFoundException::new);
        List<Word> allUserWords = wordRepository.findAllByUser_id(loggedUserId);
        int allUserWordsSize = allUserWords.size();
        int learnedWords = (int)allUserWords.stream()
                .filter(word -> word.getPriority() == 5)
                .count();
        user.setLearnedWords(learnedWords);
        user.setWordsToLearn(allUserWordsSize - learnedWords);

    }
}
