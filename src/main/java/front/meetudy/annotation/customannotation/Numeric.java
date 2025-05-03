package front.meetudy.annotation.customannotation;

import front.meetudy.annotation.customvalidator.NumericValidator;
import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

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
