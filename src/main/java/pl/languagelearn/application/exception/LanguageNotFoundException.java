package pl.languagelearn.application.exception;

public class LanguageNotFoundException extends RuntimeException{
    private final static String DEFAULT_MESSAGE = "Nie znaleziono języka o podanej nazwie.";

    public LanguageNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public LanguageNotFoundException(String message) {
        super(message);
    }
}
