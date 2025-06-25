package front.meetudy.annotation.customannotation;

import java.lang.annotation.*;

/**
 * 특수문자 및 xss annotation
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sanitize {
    Class<?>[] groups() default { };
}