package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.Numeric;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.regex.Pattern;

public class NumericValidator implements ConstraintValidator<Numeric,String> {
    private final Pattern pattern = Pattern.compile("^[0-9]+$");
    private final MessageSource messageSource;

    private int min;
    private int max;
    private int mid;
    private boolean numberEquals;
    private String patternMessageKey;  // 정규식
    private String messageKey;         // 자릿수

    public NumericValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(Numeric constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.mid = constraintAnnotation.mid();
        this.numberEquals = constraintAnnotation.numberEquals();
        this.patternMessageKey = constraintAnnotation.message();
        this.messageKey = constraintAnnotation.messageKey();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // 공백일 땐 이 유효성 검사는 skip → NotBlank에서 처리
        }

        // 정규식
        if (!pattern.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            String message = messageSource.getMessage(stripBraces(patternMessageKey), null, Locale.getDefault());
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        //자릿수 체크 numberEquals true 일 경우 자릿수 같은지 체크
        if(numberEquals) {
            if(value.length()!= mid) {
                context.disableDefaultConstraintViolation();
                String message = messageSource.getMessage(messageKey, new Object[]{mid}, Locale.getDefault());
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            return pattern.matcher(value).matches();
        } else {
            if (value.length() < min || value.length() > max) {
                context.disableDefaultConstraintViolation();
                String message = messageSource.getMessage(messageKey, new Object[]{min, max}, Locale.getDefault());
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            return pattern.matcher(value).matches();
        }

    }

    private String stripBraces(String key) {
        return key.replaceAll("[{}]", "");
    }
}
