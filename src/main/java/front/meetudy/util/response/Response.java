package front.meetudy.util.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import front.meetudy.constant.error.ErrorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 공통 응답 포맷팅
 * @param <T>
 */
@Getter
@AllArgsConstructor
@Schema(description = "API 공통 응답 포맷")
public class Response<T> {

    @Schema(description = "결과 코드", example = "SUCCESS")
    private String resultCode;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int httpCode;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "에러 코드", example = "ERR_XXX")
    private String errCode;

    @Schema(description = "에러 코드 메시지", example = "값이 없습니다.")
    private String errCodeMsg;

    @Schema(description = "응답 데이터")
    private  T data;

    @Schema(description = "시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private static final String SUCCESS_CODE = "SUCCESS";

    private static final String ERROR_CODE = "ERROR";

    /**
     * 일반적인 에러
     * @param status
     * @param message
     * @param messageOrData
     * @return
     * @param <T>
     */
    public static <T> ResponseEntity<Response<T>> error(HttpStatus status,String message,ErrorEnum errorEnum, T messageOrData) {
        return ResponseEntity.status(status).body(new Response<>("ERROR", status.value(), message, errorEnum.toString(), errorEnum.getValue(), messageOrData,LocalDateTime.now()));
    }

    /**
     * 변수 유효성 검사 에러
     * @param status
     * @param message
     * @param field
     * @return
     */
    public static ResponseEntity<Response<ValidationErrorResponse>> validationError(HttpStatus status, String message,ErrorEnum errorEnum , String field) {
        return ResponseEntity.status(status).body(new Response<>(ERROR_CODE, status.value(), message,errorEnum.toString(),errorEnum.getValue(), new ValidationErrorResponse(field, message),LocalDateTime.now()));
    }
    public static ResponseEntity<Response<List<ValidationErrorResponse>>> validationErrorList(HttpStatus status, String message, ErrorEnum errorEnum, List<ValidationErrorResponse> errors) {
        return ResponseEntity.status(status).body(
                new Response<>(
                        ERROR_CODE,
                        status.value(),
                        message,
                        errorEnum.toString(),
                        errorEnum.getValue(),
                        errors,
                        LocalDateTime.now()
                )
        );
    }
    public static <T> ResponseEntity<Response<T>> ok(String message, T data) {
        return ResponseEntity.status(HttpStatus.OK).body(successRead(message, data));
    }

    public static <T> ResponseEntity<Response<T>> create(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(successCreate(message, data));
    }

    public static <T> ResponseEntity<Response<T>> update(String message, T data) {
        return ResponseEntity.status(HttpStatus.OK).body(successUpdate(message, data));
    }

    public static <T> ResponseEntity<Response<T>> delete(String message, T data) {
        return ResponseEntity.status(HttpStatus.OK).body(successDelete(message, data));
    }

    public static ResponseEntity<Void> deleteNoContent() {
        return ResponseEntity.noContent().build();
    }

    protected static Response<String> error(int httpCode, ErrorEnum errorEnum ,String message) {
        return new Response<>(ERROR_CODE, httpCode, message,errorEnum.toString(),errorEnum.getValue(), null,LocalDateTime.now());
    }


    protected static <T> Response<T> successRead(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.OK.value(), message,null,null,data,LocalDateTime.now());
    }

    protected static <T> Response<T> successCreate(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.CREATED.value(), message,null,null, data,LocalDateTime.now());
    }

    protected static <T> Response<T> successUpdate(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.OK.value(), message,null,null, data,LocalDateTime.now());
    }

    protected static <T> Response<T> successDelete(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.OK.value(), message,null,null, data,LocalDateTime.now());
    }


    @Override
    public String toString() {
        return "Response{" +
                "resultCode='" + resultCode + '\'' +
                ", httpCode=" + httpCode +
                ", message='" + message + '\'' +
                ", errCode='" + errCode + '\'' +
                ", errCodeMsg='" + errCodeMsg + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
