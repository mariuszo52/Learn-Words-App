package pl.languagelearn.application.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class UserRegisterDto {
    private Long id;
    @Email(message = "Email musi być poprawnie sformatowanym adresem np:email@example.com")
    private String email;
    @Pattern(message = "Niepoprawne hasło", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String password;

    public UserRegisterDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserRegisterDto(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
}
