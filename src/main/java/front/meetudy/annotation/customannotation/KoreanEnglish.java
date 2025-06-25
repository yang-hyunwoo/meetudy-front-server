package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.KoreanEnglishValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 한글 / 영문 annotation
 * KoreanEnglish(min = 최소 자릿수, max = 최대 자릿수, message = "정규식메시지",messageKey = "범위메시지",group= 단일 에러 리턴시 순서)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = KoreanEnglishValidator.class)
@Documented
public @interface KoreanEnglish {

    String message() default "default.koEnPermit";
    String messageKey() default "default.range";
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
