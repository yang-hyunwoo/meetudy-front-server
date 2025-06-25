package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * enum class 유효성 검사
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface EnumValidation {

    Class<? extends Enum<?>> enumClass();
    String message() default "올바른 값이 아닙니다.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
