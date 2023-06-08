package pl.languagelearn.application.exception;

public class CategoryNotFoundException extends RuntimeException{
    private final static String DEFAULT_MESSAGE = "Nie znaleziono kategorii.";

    public CategoryNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
