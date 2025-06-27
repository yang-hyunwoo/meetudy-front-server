package front.meetudy.exception;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import front.meetudy.util.response.ValidationErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler {

    private final MessageUtil messageUtil;

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

    //TODO 어떤 방식으로 내려줄지 고민
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<ValidationErrorResponse>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}" , e.getMessage());
        return Response.error(BAD_REQUEST, e.getMessage(), ERR_002, null);
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
        return Response.error(BAD_REQUEST, messageUtil.getMessage("error.not.data.type.ok"), ERR_018, null);
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

    /**
     * 404 에러
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response<String>> handleNotFound(NoHandlerFoundException ex) {
        log.error("404 에러 발생",ex);
        return Response.error(NOT_FOUND, messageUtil.getMessage("error.not.fount.ok"), ERR_404, null);
    }

    /**
     * 405 에러
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<String>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.error("405 에러 발생",ex);
        return Response.error(METHOD_NOT_ALLOWED, messageUtil.getMessage("error.not.allow.method.ok"), ERR_405, null);
    }

    /**
     * 500 에러
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> handleServerError(Exception ex) {
        log.error("500 에러 발생", ex);
        return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, messageUtil.getMessage("error.server.ok"), ErrorEnum.ERR_500, null);
    }
}
