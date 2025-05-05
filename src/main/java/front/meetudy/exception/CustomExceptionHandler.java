package front.meetudy.exception;

import front.meetudy.util.response.Response;
import front.meetudy.util.response.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    /**
     * 일반적인 에러
     * @param e
     * @return
     */
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<Response<String>> handleCustomApiException(CustomApiException e) {
        log.error("CustomApiException: {}", e.getMessage());
        return Response.error(e.getStatus(), e.getMessage(), e.getErrorEnum(), null);
    }

    /**
     * 변수 단일 유효성 에러 리턴
     * @param e
     * @return
     */
    @ExceptionHandler(CustomApiFieldException.class)
    public ResponseEntity<Response<ValidationErrorResponse>> handleCustomApiFieldException(CustomApiFieldException e) {
        log.error("CustomApiFieldException: {}", e.getMessage());
        return Response.validationError(e.getStatus(),e.getMessage(),e.getErrorEnum(),e.getField());
    }

    /**
     * 변수 리스트 유효성 에러 리턴
     * @param e
     * @return
     */
    @ExceptionHandler(CustomApiFieldListException.class)
    public ResponseEntity<Response<List<ValidationErrorResponse>>> handleCustomApiFieldListException(CustomApiFieldListException e) {
        log.error("CustomApiFieldListException: {}", e.getMessage());
        return Response.validationErrorList(e.getStatus(), e.getMessage(), e.getErrorEnum(), e.getErrors());
    }

}
