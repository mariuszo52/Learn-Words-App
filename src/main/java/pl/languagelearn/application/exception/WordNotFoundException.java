package pl.languagelearn.application.exception;

public class WordNotFoundException extends RuntimeException{
    private final static String DEFAULT_MESSAGE = "Nie znaleziono słowa o podanym id.";

    public WordNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public WordNotFoundException(String message) {
        super(message);
    }
}
