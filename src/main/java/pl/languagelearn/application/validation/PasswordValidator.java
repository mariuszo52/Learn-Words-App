package pl.languagelearn.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<BcryptPassword, String> {
    private static final String BCRYPT_REGEX = "^\\$2[ayb]\\$.{56}$";
    private static final String PREFIX = "{bcrypt}";


    @Override
    public void initialize(BcryptPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile(BCRYPT_REGEX);
        String passwordHash = password.substring(PREFIX.length());
        Matcher matcher = pattern.matcher(passwordHash);
        return matcher.matches();
    }
}
