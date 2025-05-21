package front.meetudy.dto.request.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.error.ValidationType;
import front.meetudy.annotation.customannotation.*;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import static front.meetudy.annotation.ValidationGroups.*;

//TODO: NotBlank를 커스텀 어노테이션에 추가할지는 추후 생각

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class JoinMemberReqDto {

    @Schema(description = "프로필이미지ID", example = "1")
    private Long profileImageId;

    @Schema(description = "이메일", example = "xxx@naver.com")
    @NotBlank(message = "{email.notBlank}",groups = Step1.class)
    @Email(groups = Step1.class)
    private String email;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "{name.notBlank}", groups = Step2.class)
    @KoreanEnglish(min = 1, max = 50, message = "{name.pattern}",messageKey = "name.range", groups = Step2.class)
    @Sanitize(groups = Step2.class)
    private String name;

    @Schema(description = "닉네임", example = "홍길동")
    @NotBlank(message = "{nickname.notBlank}", groups = Step3.class)
    @KoreanEnglish(min = 1, max = 30, message = "{nickname.pattern}",messageKey = "nickname.range", groups = Step3.class)
    @Sanitize(groups = Step3.class)
    private String nickName;

    @Schema(description = "생년월일", example = "19990101")
    @NotBlank(message = "{birth.notBlank}",groups = Step4.class)
    @Numeric(message = "{birth.pattern}",
            messageKey = "birth.range",
            mid=8,
            numberEquals = true,
            groups = Step4.class)
    private String birth;

    @Schema(description = "휴대폰번호", example = "01011112222")
    @NotBlank(message = "{phone.notBlank}",groups = Step5.class)
    @PhoneNumber(groups = Step5.class)
    private String phoneNumber;

    @Schema(description = "비밀번호", example = "xxx")
    @NotBlank(message = "{password.notBlank}",groups = Step6.class)
    @Password(message = "{password.pattern}", groups = Step6.class)
    private String password;

    @Schema(description = "이메일동의여부", example = "true")
    @JsonProperty("isEmailAgreed")
    private boolean isEmailAgreed;

    @Schema(description = "소셜타입", example = "NORMAL")
    @EnumValidation(enumClass = MemberProviderTypeEnum.class,groups = Step7.class)
    private String provider;

    @Schema(description = "소셜ID", example = "asdf")
    private String providerId;

    private String recaptchaToken;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.createMember(
                profileImageId,
                email,
                name,
                nickName,
                birth,
                phoneNumber,
                passwordEncoder.encode(password),
                isEmailAgreed
        );
    }

    @Override
    public String toString() {
        return "JoinMemberReqDto{" +
                "profileImageId=" + profileImageId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", birth='" + birth + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isEmailAgreed=" + isEmailAgreed +
                '}';
    }
}



