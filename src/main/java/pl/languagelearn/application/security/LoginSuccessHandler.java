package pl.languagelearn.application.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.languagelearn.application.user.UserStatsService;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final UserStatsService userStatsService;

    public LoginSuccessHandler(UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        userStatsService.updateDaysInARowStat();
        super.onAuthenticationSuccess(request, response, authentication);
    }
}


