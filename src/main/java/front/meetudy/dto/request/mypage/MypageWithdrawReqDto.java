package front.meetudy.dto.request.mypage;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.annotation.customannotation.Password;
import front.meetudy.constant.error.ValidationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static front.meetudy.annotation.ValidationGroups.Step1;
import static front.meetudy.annotation.ValidationGroups.Step2;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class MypageWithdrawReqDto {

    @Schema(description = "비밀번호", example = "xxx")
    @NotBlank(message = "{password.notBlank}",groups = Step1.class)
    private String currentPw;
}
