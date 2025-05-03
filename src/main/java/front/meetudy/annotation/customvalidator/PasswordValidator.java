package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private final Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,20}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 공백일 땐 이 유효성 검사는 skip → NotBlank에서 처리
        }
        context.disableDefaultConstraintViolation();
        return pattern.matcher(value).matches();
    }
}