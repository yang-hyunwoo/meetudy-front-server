package front.meetudy.util.aop;

import front.meetudy.annotation.DefaultValidationOrder;
import front.meetudy.annotation.customannotation.ValidationSequence;
import front.meetudy.annotation.SequentialValidator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidationGroupAspect {

    private final SequentialValidator validator;

    @Around("execution(* *..*Controller.*(..))")
    public Object autoValidate(ProceedingJoinPoint joinPoint) throws Throwable {
        for (Object arg : joinPoint.getArgs()) {
            if (arg == null) continue;

            Class<?>[] groupOrder = getValidationOrder(arg.getClass());
            if (groupOrder != null) {
                validator.validate(arg, groupOrder);
            }
        }
        return joinPoint.proceed();
    }

    private Class<?>[] getValidationOrder(Class<?> clazz) {
        if (!clazz.getSimpleName().endsWith("ReqDto")) return null;
        if (!hasValidationGroups(clazz)) return null;

        ValidationSequence sequence = clazz.getAnnotation(ValidationSequence.class);
        return (sequence != null) ? sequence.value() : DefaultValidationOrder.ORDER;
    }

    private boolean hasValidationGroups(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                try {
                    Object groups = annotation.annotationType().getMethod("groups").invoke(annotation);
                    if (groups instanceof Class<?>[] g && g.length > 0 && !g[0].equals(Default.class)) {
                        return true;
                    }
                } catch (NoSuchMethodException ignored) {
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
}
