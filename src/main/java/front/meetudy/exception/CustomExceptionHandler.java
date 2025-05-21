package front.meetudy.exception;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.util.response.Response;
import front.meetudy.util.response.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<ValidationErrorResponse>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
       log.error("DataIntegrityViolationException: {}" , e.getMessage());
        return Response.error(BAD_REQUEST, "데이터 타입이 올바르지 않습니다.", ErrorEnum.ERR_018, null);
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
