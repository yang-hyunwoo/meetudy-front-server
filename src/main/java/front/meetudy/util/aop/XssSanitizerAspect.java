package front.meetudy.util.aop;

import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.util.xss.XssSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Aspect
@Component
public class XssSanitizerAspect {

    // ✅ 모든 controller 패키지의 메서드 실행 전에 적용
    @Before("execution(* *..*Controller.*(..))")
    public void sanitizeFields(JoinPoint joinPoint) throws IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null) continue;
            sanitizeObjectFields(arg);
        }
    }

    private void sanitizeObjectFields(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Sanitize.class) && field.getType().equals(String.class)) {
                field.setAccessible(true);
                String original = (String) field.get(obj);
                if (original != null) {
                    String sanitized = XssSanitizer.sanitize(original);
                    field.set(obj, sanitized);
                }
            }
        }
    }
}