package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 비밀번호 검증 validator
 * 정규식 검사
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    private final Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,20}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 공백일 땐 이 유효성 검사는 skip → NotBlank에서 처리
        }
        if (!pattern.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{password.pattern}") // 또는 실제 메시지
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}