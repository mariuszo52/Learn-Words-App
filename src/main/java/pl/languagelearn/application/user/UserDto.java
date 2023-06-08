package pl.languagelearn.application.user;

public class UserDto {
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    private long id;
    private String email;
    private String password;
    private String role;
    private boolean isAccountNotLocked;
    private String confirmationToken;

    public UserDto(long id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public boolean isAccountNotLocked() {
        return isAccountNotLocked;
    }

    public void setAccountNotLocked(boolean accountNotLocked) {
        isAccountNotLocked = accountNotLocked;
    }

    public UserDto(long id, String email, String password,
                   String role, boolean isAccountNotLocked,
                   String confirmationToken) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isAccountNotLocked = isAccountNotLocked;
        this.confirmationToken = confirmationToken;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
