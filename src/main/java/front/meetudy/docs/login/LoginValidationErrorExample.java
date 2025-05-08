package front.meetudy.docs.login;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "로그인 실패",
        content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "필수값 오류", value = LoginSwaggerJoinProvider.LOGIN_VALIDATION_PW_EMAIL_INVALID),
                   })
)
@ApiResponse(
        responseCode = "401",
        description = "로그인 실패",
        content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "비밀번호 오류", value = LoginSwaggerJoinProvider.LOGIN_VALIDATION_PW_EMAIL_FAIL),
                @ExampleObject(name = "5회 오류로 인한 계정 잠김", value = LoginSwaggerJoinProvider.LOGIN_VALIDATION_PW_LOCKED)
        })
)
public @interface LoginValidationErrorExample {
}
