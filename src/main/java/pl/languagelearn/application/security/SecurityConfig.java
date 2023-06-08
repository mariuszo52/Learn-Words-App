package pl.languagelearn.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import pl.languagelearn.application.user.CustomUserDetailsService;
import pl.languagelearn.application.user.UserDto;

@Configuration
class SecurityConfig{
    private final LoginSuccessHandler loginSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final LogoutHandler logoutHandler;
    private final CustomUserDetailsService customUserDetailsService;

    SecurityConfig(LoginSuccessHandler loginSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler, LogoutHandler logoutHandler, CustomUserDetailsService customUserDetailsService) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.logoutHandler = logoutHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.authorizeHttpRequests(request -> request
                .requestMatchers("/admin-panel/**").hasAnyAuthority(UserDto.ADMIN_ROLE)
                .requestMatchers("/user-panel/**").hasAnyAuthority(UserDto.USER_ROLE)
                .requestMatchers("/", "/learn", "/register").permitAll()
                .requestMatchers("/styles/main.css", "/img/**").permitAll()
                .requestMatchers("/register-confirm/**", "/send-email/**", "/remember-pass").permitAll()
                .requestMatchers("/scripts.js").permitAll()
                .anyRequest().authenticated());
        security.addFilterBefore(new HiddenHttpMethodFilter(), BasicAuthenticationFilter.class);
        security.formLogin(form -> form
                        .successHandler(loginSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .loginPage("/login").permitAll());
        security.logout(
                logout ->logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                        .addLogoutHandler(logoutHandler)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/login"));
        security.csrf().ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"));
        security.headers()
                .frameOptions()
                .sameOrigin();
        return security.build();
    }


    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }




}
