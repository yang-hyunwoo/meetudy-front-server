package front.meetudy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiFieldException extends RuntimeException{

    private final HttpStatus status;
    private final String field;

    public CustomApiFieldException(HttpStatus status, String message , String field) {
        super(message);
        this.status = status;
        this.field = field;
    }

}
