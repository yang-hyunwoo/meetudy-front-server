package front.meetudy.user.dto.request.mypage;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.annotation.customannotation.KoreanEnglish;
import front.meetudy.annotation.customannotation.PhoneNumber;
import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.constant.error.ValidationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class MypageDetailChgReqDto {

    @Schema(description = "닉네임", example = "홍길동")
    @NotBlank(message = "{nickname.notBlank}", groups = Step1.class)
    @KoreanEnglish(min = 1, max = 30, message = "{nickname.pattern}",messageKey = "nickname.range", groups = Step1.class)
    @Sanitize(groups = Step1.class)
    private String nickname;

    @Schema(description = "휴대폰번호", example = "01011112222")
    @NotBlank(message = "{phone.notBlank}",groups = Step2.class)
    @PhoneNumber(groups = Step2.class)
    private String phoneNumber;

    @Schema(description = "프로필 id" ,example = "1")
    private Long profileImageId;

}
