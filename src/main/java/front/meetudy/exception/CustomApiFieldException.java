package front.meetudy.exception;

import front.meetudy.constant.error.ErrorEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiFieldException extends RuntimeException{

    private final HttpStatus status;

    private final String field;

    private final ErrorEnum errorEnum;

    public CustomApiFieldException(HttpStatus status,
                                   String message,
                                   ErrorEnum errorEnum,
                                   String field
    ) {
        super(message);
        this.status = status;
        this.field = field;
        this.errorEnum = errorEnum;
    }

}
