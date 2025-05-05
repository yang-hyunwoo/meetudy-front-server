package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.NumericValidator;
import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

/**
 * 숫자 커스텀 어노테이션
 * 자릿수 정확하게 : @Numeric(message = "정규식메세지", messageKey = "범위메시지", mid=자릿수, numberEquals = true)
 * 자릿수 1~10 : @Numeric(message = "정규식메세지", messageKey = "범위메시지", min=최소 자리수 , max=최대자리수)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumericValidator.class)
@Documented
public @interface Numeric {
    String message() default "{default.numeric}";
    String messageKey() default "default.range";
    int min() default 0;
    int mid() default 0 ;
    int max() default Integer.MAX_VALUE;
    boolean numberEquals() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
