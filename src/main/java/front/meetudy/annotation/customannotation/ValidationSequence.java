package front.meetudy.annotation.customannotation;

import java.lang.annotation.*;

/**
 * 유효성 검사 순서 annotation
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationSequence {
    Class<?>[] value();
}
