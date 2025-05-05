package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Email;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 이메일 유효검사
 *
 */
public class EmailValidator implements ConstraintValidator<Email,String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 공백일 땐 이 유효성 검사는 skip → NotBlank에서 처리
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }
}