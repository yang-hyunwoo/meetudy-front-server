package front.meetudy.exception;

import front.meetudy.util.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<Response<String>> handleCustomApiException(CustomApiException e) {
        log.error("CustomApiException: {}", e.getMessage());

        return Response.error(e.getStatus(),e.getMessage(),null);
    }
}
