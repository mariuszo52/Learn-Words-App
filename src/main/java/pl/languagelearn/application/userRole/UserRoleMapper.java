package pl.languagelearn.application.userRole;

import pl.languagelearn.application.user.User;

import java.util.Set;
import java.util.stream.Collectors;

class UserRoleMapper {
    static UserRoleDto map(UserRole userRole){
        UserRoleDto userRoleDto = new UserRoleDto();
        userRoleDto.setId(userRole.getId());
        userRoleDto.setName(userRole.getName());
        userRoleDto.setDescription(userRole.getDescription());
        Set<Long> usersIds = userRole.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        userRoleDto.setUsersIds(usersIds);
        return userRoleDto;
    }
}
