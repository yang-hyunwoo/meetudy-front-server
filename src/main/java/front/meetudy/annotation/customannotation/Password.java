package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 비밀번호 annotation
 *  Password(message=메시지 , group= 단일 에러 리턴시 순서)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface Password {

    String message() default "{password.pattern}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
