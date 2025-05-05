package front.meetudy.annotation;

import front.meetudy.constant.error.ValidationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationMode {
    ValidationType value() default ValidationType.SINGLE;
}

