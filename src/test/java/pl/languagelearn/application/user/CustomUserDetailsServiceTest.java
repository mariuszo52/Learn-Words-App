package pl.languagelearn.application.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.languagelearn.application.userRole.UserRole;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private User user;
    @Mock
    private UserRole userRole;
    private CustomUserDetailsService customUserDetailsService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
        when(userRole.getName()).thenReturn("ADMIN");
        when(user.getId()).thenReturn(1L);
        when(user.getUserRole()).thenReturn(userRole);
        when(user.getEmail()).thenReturn("email@email.com");
        when(user.getPassword()).thenReturn("password");
        when(userRepository.findUserByEmail("email@email.com"))
                .thenReturn(Optional.of(user));
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }
    @Test
    void shouldReturnCorrectUserDetails() {
        //given

        //when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("email@email.com");
        //then
        assertThat(userDetails.getUsername()).isEqualTo("email@email.com");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities().size()).isEqualTo(1);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWithCorrectMessage() {
        //given
        String wrongEmail = "wrongemail@email.com";
        //when //then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(wrongEmail));
        assertThat(exception.getMessage()).isEqualTo("Podano zły adres email.");
    }
}