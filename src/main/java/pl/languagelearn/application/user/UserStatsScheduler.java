package pl.languagelearn.application.user;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class UserStatsScheduler {
    private final UserRepository userRepository;

    UserStatsScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Scheduled(cron = "@daily")
    public void resetDailyStats(){
        userRepository.findAll()
                .forEach(user -> {
                    user.setRepeatedWordsToday(0);
                    user.setTimeToday(0);
                });
    }

    @Transactional
    @Scheduled(cron = "@weekly")
    public void resetWeekStats(){
        userRepository.findAll()
                .forEach(user -> user.setNewWordsWeek(0));
    }
}
