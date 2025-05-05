package front.meetudy.annotation;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.exception.CustomApiFieldException;
import front.meetudy.exception.CustomApiFieldListException;
import front.meetudy.util.response.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static front.meetudy.constant.error.ErrorEnum.*;

/**

 */
@Component
@RequiredArgsConstructor
public class SequentialValidator {

    private final Validator validator;

    /**
     * dto 유효성 검사 순차적으로 그룹별로 수행하여
     * 가장 먼저 실패하는 검증 하나만 CustomApiFieldException 예외로 던짐
     * @param dto
     * @param groupOrder
     * @param <T>
     */
    public <T> void validate(T dto, Class<?>[] groupOrder) {
        for (Class<?> group : groupOrder) {
            Set<ConstraintViolation<T>> violations = validator.validate(dto, group);
            if (!violations.isEmpty()) {
                ConstraintViolation<T> v = violations.iterator().next();
                String field = v.getPropertyPath().toString();
                String annotationName = v.getConstraintDescriptor().getAnnotation()
                        .annotationType().getSimpleName();

                // 어노테이션 + 필드명 기반으로 에러코드 동적 결정
                ErrorEnum errorEnum = mapErrorEnum(annotationName);
                throw new CustomApiFieldException(HttpStatus.BAD_REQUEST,v.getMessage(), errorEnum,field);
            }
        }
    }

    /**
     * dto 유효성 검사 순차적으로 그룹별로 수행하여
     * 모든 검증 에러  CustomApiFieldListException 예외로 던짐
     */
    public <T> void validateAll(T dto, Class<?>[] groupOrder) {
        List<ValidationErrorResponse> errorList = new ArrayList<>();
        for (Class<?> group : groupOrder) {
            Set<ConstraintViolation<T>> violations = validator.validate(dto, group);
            for (ConstraintViolation<T> v : violations) {
                String field = v.getPropertyPath().toString();
                errorList.add(new ValidationErrorResponse(field, v.getMessage()));
            }
        }
        if (!errorList.isEmpty()) {
            throw new CustomApiFieldListException(HttpStatus.BAD_REQUEST, "유효성 검사 실패", ERR_002, errorList);
        }
    }

    private ErrorEnum mapErrorEnum( String annotation) {
        return switch (annotation) {
            case "NotBlank" -> ERR_001;
            default -> ERR_002;
        };
    }

}