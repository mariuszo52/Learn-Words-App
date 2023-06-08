package pl.languagelearn.application.userRole;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class UserRoleServiceTest {
    @Mock UserRoleRepository userRoleRepository;
    private UserRoleService userRoleService;
    private AutoCloseable autoCloseable;
    @BeforeEach
    void setUp(){
        autoCloseable = MockitoAnnotations.openMocks(this);
        userRoleService = new UserRoleService(userRoleRepository);
    }
    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }
    @Test
    void shouldReturn1WhileFindAllElements(){
        //given
        UserRole userRole1 = new UserRole(1L, "role1", "descr", Collections.emptySet());
        Iterable<UserRole> allUserRoles = List.of(userRole1);
        when(userRoleRepository.findAll()).thenReturn(allUserRoles);
        //when
        Set<UserRoleDto> result = userRoleService.getAllRoles();
        //then
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    void shouldReturn3WhileFindAllElements(){
        //given
        UserRole userRole1 = new UserRole(1L, "role1", "descr", Collections.emptySet());
        UserRole userRole2 = new UserRole(2L, "role2", "descr", Collections.emptySet());
        UserRole userRole3 = new UserRole(3L, "role3", "descr", Collections.emptySet());
        Iterable<UserRole> allUserRoles = List.of(userRole1, userRole2, userRole3);
        when(userRoleRepository.findAll()).thenReturn(allUserRoles);
        //when
        Set<UserRoleDto> result = userRoleService.getAllRoles();
        //then
        assertThat(result.size()).isEqualTo(3);
    }
    @Test
    void shouldReturn5WhileFindAllElements(){
        //given
        UserRole userRole1 = new UserRole(1L, "role1", "descr", Collections.emptySet());
        UserRole userRole2 = new UserRole(2L, "role2", "descr", Collections.emptySet());
        UserRole userRole3 = new UserRole(3L, "role3", "descr", Collections.emptySet());
        UserRole userRole4 = new UserRole(4L, "role4", "descr", Collections.emptySet());
        UserRole userRole5 = new UserRole(5L, "role5", "descr", Collections.emptySet());
        Iterable<UserRole> allUserRoles = List.of(userRole1, userRole2, userRole3, userRole4, userRole5);
        when(userRoleRepository.findAll()).thenReturn(allUserRoles);
        //when
        Set<UserRoleDto> result = userRoleService.getAllRoles();
        //then
        assertThat(result.size()).isEqualTo(5);
    }
    @Test
    void shouldReturnTrueWhenNotFoundAnyUserRole(){
        //given
        when(userRoleRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        Set<UserRoleDto> result = userRoleService.getAllRoles();
        //then
        assertTrue(result.isEmpty());
    }

}