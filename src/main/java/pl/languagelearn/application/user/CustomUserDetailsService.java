package pl.languagelearn.application.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userRepository.findUserByEmail(username)
                .map(this::createCustomUserDetails)
                .orElseThrow(()-> new UsernameNotFoundException("Podano zły adres email."));
    }


    private CustomUserDetails createCustomUserDetails(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getUserRole().getName()));
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(),authorities, user.isAccountNotLocked());
    }

}
