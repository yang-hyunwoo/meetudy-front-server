package front.meetudy.annotation;

import front.meetudy.exception.CustomApiException;
import front.meetudy.exception.CustomApiFieldException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SequentialValidator {

    private final Validator validator;

    public <T> void validate(T dto, Class<?>[] groupOrder) {
        for (Class<?> group : groupOrder) {
            Set<ConstraintViolation<T>> violations = validator.validate(dto, group);
            if (!violations.isEmpty()) {
                ConstraintViolation<T> v = violations.iterator().next();
                String field = v.getPropertyPath().toString();
                throw new CustomApiFieldException(HttpStatus.BAD_REQUEST,v.getMessage(),field);
            }
        }
    }
}