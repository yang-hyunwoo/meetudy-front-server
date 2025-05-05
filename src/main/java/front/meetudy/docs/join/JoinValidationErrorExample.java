package front.meetudy.docs.join;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "유효성 검사 실패",
        content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "공백 오류", value = JoinSwaggerJoinProvider.JOIN_VALIDATION_NOT_NULL),
                @ExampleObject(name = "유효성 오류", value = JoinSwaggerJoinProvider.JOIN_VALIDATION_VAILDATION_ERROR)
        })
)
public @interface JoinValidationErrorExample {
}
