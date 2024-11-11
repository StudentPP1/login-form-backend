package github.studentpp1.advancedloginform.util.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordFieldName;
    private String passwordMatchFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        // get values from annotation
        passwordFieldName = constraintAnnotation.passwordField();
        passwordMatchFieldName = constraintAnnotation.passwordConfirmationField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Class<?> clazz = value.getClass();
            // get current values in class fields
            Field passwordField = clazz.getDeclaredField(passwordFieldName);
            Field passwordMatchField = clazz.getDeclaredField(passwordMatchFieldName);

            // робим одноразову перевірку (аннотація не запам'ятовує стан змінної)
            passwordField.setAccessible(true);
            passwordMatchField.setAccessible(true);

            // check if they are match
            String password = (String) passwordField.get(value);
            String passwordMatch = (String) passwordMatchField.get(value);

            return password != null && password.equals(passwordMatch);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}