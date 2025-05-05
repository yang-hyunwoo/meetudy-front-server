package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 휴대폰 번호 커스텀 어노테이션
 * 1.010 체크
 * 2.숫자 체크
 * 3.11자리 체크

 */
@Component
@RequiredArgsConstructor
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private final MessageSource messageSource;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 공백일 땐 이 유효성 검사는 skip → NotBlank에서 처리
        }

        if (!value.matches("^010\\d{8}$")) {
            if (!value.startsWith("010")) {
                setMessage(context, "phone.start");
                return false;
            }
            if (!value.matches("^\\d+$")) {
                setMessage(context, "phone.numeric");
                return false;
            }
            if (value.length() != 11) {
                setMessage(context, "phone.length");
                return false;
            }
        }

        return true;
    }

    private void setMessage(ConstraintValidatorContext context, String key) {
        context.disableDefaultConstraintViolation();
        String message = messageSource.getMessage(key, null, Locale.getDefault());
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}

