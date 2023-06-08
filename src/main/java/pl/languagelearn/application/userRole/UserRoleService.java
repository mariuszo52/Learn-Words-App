package pl.languagelearn.application.userRole;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

   public Set<UserRoleDto> getAllRoles(){
        Spliterator<UserRole> userRoleSpliterator = userRoleRepository.findAll().spliterator();
        return StreamSupport.stream(userRoleSpliterator, false)
                .map(UserRoleMapper::map)
                .collect(Collectors.toSet());

    }
}
