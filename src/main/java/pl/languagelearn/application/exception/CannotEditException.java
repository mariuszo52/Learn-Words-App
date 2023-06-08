package pl.languagelearn.application.exception;

public class CannotEditException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "Nie można edytować tego obiektu.";
    public CannotEditException() {
        super(DEFAULT_MESSAGE);
    }

    public CannotEditException(String message) {
        super(message);
    }
}
