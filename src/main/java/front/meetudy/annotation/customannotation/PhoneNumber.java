package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 휴대폰번호  annotation
 * PhoneNumber(message=메시지,group= 단일 에러 리턴시 순서)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface PhoneNumber {

    String message() default "{phone.pattern}"; // default 메시지 키
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}