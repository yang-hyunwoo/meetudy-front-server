package front.meetudy.exception;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.util.response.ValidationErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class CustomApiFieldListException extends RuntimeException {

    private final HttpStatus status;

    private final ErrorEnum errorEnum;

    private final List<ValidationErrorResponse> errors;

    public CustomApiFieldListException(HttpStatus status,
                                       String message,
                                       ErrorEnum errorEnum,
                                       List<ValidationErrorResponse> errors
    ) {
        super(message);
        this.status = status;
        this.errorEnum = errorEnum;
        this.errors = errors;
    }

}
