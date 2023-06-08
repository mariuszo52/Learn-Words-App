package pl.languagelearn.application.exception;

public class UserNotFoundException extends RuntimeException{
    private final static String DEFAULT_MESSAGE = "Nie znaleziono użytkownika.";
    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
