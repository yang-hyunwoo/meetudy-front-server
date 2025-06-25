package front.meetudy.annotation.customannotation;

import java.lang.annotation.*;

/**
 * 사용자 정보 체크 annotation
 * ex) @CurrentMember(required = false , true) default true
 *  false : 인증 시 체크 하지 않음
 *  true : 무조건 체크
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentMember {

    boolean required() default true;
}
