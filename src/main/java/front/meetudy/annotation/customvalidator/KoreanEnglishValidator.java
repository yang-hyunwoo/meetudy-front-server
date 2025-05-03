package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.KoreanEnglish;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class KoreanEnglishValidator implements ConstraintValidator<KoreanEnglish, String> {

    private final Pattern pattern = Pattern.compile("^[가-힣a-zA-Z]+$");
    private final MessageSource messageSource;

    private int min;
    private int max;
    private String patternMessageKey;  // 정규식
    private String messageKey;         // 자릿수

    public KoreanEnglishValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(KoreanEnglish constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.patternMessageKey = constraintAnnotation.message(); // 정규식 메시지 키
        this.messageKey = constraintAnnotation.messageKey(); // 자릿수 메시지 키
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // 정규식
        if (!pattern.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            String message = messageSource.getMessage(stripBraces(patternMessageKey), null, Locale.getDefault());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        // 자릿수
        if (value.length() < min || value.length() > max) {
            context.disableDefaultConstraintViolation();
            String message = messageSource.getMessage(messageKey, new Object[]{min, max}, Locale.getDefault());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }

    private String stripBraces(String key) {
        return key.replaceAll("[{}]", "");
    }
}
