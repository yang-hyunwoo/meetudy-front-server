package front.meetudy.dto.request.member;

import front.meetudy.annotation.customannotation.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {

    @Schema(description = "이메일", example = "xxx@naver.com")
    @NotBlank(message = "{email.notBlank}",groups = Step1.class)
    @Email(groups = Step1.class)
    private String email;

    @Schema(description = "비밀번호", example = "xxx")
    @NotBlank(message = "{password.notBlank}",groups = Step2.class)
    private String password;

    private boolean chk;

}
