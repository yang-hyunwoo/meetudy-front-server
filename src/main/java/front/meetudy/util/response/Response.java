package front.meetudy.util.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @Schema(description = "응답 데이터")
    private  T data;

    private static final String SUCCESS_CODE = "SUCCESS";

    private static final String ERROR_CODE = "ERROR";

    public static <T> ResponseEntity<Response<T>> error(String code, HttpStatus status,String message, T messageOrData) {
        return ResponseEntity.status(status).body(new Response<>(code, status.value(), message, messageOrData));
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

    protected static Response<String> error(int httpCode, String message) {
        return new Response<>(ERROR_CODE, httpCode, message, null);
    }

    protected static <T> Response<T> successRead(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.OK.value(), message, data);
    }

    protected static <T> Response<T> successCreate(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.CREATED.value(), message, data);
    }

    protected static <T> Response<T> successUpdate(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.OK.value(), message, data);
    }

    protected static <T> Response<T> successDelete(String message, T data) {
        return new Response<>(SUCCESS_CODE, HttpStatus.OK.value(), message, data);
    }


    @Override
    public String toString() {
        return "Response{" +
                "resultCode='" + resultCode + '\'' +
                ", httpCode=" + httpCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
