package pl.languagelearn.application.userRole;

import java.util.HashSet;
import java.util.Set;

public class UserRoleDto {

    private Long id;
    private String name;
    private String description;
    private Set<Long> usersIds = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(Set<Long> usersIds) {
        this.usersIds = usersIds;
    }
}
