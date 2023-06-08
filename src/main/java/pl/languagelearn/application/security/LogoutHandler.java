package pl.languagelearn.application.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.languagelearn.application.user.UserStatsService;

import java.time.LocalDateTime;

@Component
class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    private final UserStatsService userStatsService;

    LogoutHandler(UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LocalDateTime logoutTime = LocalDateTime.now();
        userStatsService.updateLearningTime(logoutTime);
        System.out.println(logoutTime);
    }
}
