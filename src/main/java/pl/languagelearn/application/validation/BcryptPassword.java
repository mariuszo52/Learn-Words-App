package pl.languagelearn.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = PasswordValidator.class)
public @interface BcryptPassword {
    String message() default "Value is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
