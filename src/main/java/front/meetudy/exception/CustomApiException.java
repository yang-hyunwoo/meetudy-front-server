package front.meetudy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomApiException extends RuntimeException{

    private final HttpStatus status;

    public CustomApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
