package front.meetudy.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecaptchaRes {

    @Schema(description = "리캡차 성공 여부" , example = "true")
    private boolean success;

    @Schema(description = "리캡차 점수" , example = "50")
    private float score;

    private String action;

    private String hostname;

}