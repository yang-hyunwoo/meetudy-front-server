package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 비밀번호 커스텀 어노테이션
 *  @Password
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
