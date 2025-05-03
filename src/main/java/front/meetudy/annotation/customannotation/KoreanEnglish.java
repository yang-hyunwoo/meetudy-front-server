package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.KoreanEnglishValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

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
