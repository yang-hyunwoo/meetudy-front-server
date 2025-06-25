package front.meetudy.exception;

import front.meetudy.constant.error.ErrorEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiException extends RuntimeException{

    private final HttpStatus status;
    private final ErrorEnum errorEnum;

    public CustomApiException(HttpStatus status,
                              ErrorEnum errorEnum,
                              String message
    ) {
        super(message);
        this.status = status;
        this.errorEnum = errorEnum;
    }

}
