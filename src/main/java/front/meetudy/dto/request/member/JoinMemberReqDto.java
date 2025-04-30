package front.meetudy.dto.request.member;

import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.member.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Setter
public class JoinMemberReqDto {

    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해 주세요.")
    @NotEmpty
    private String name;
    @NotEmpty
    @Size(min = 4, max = 20)
    private String password;
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9]{2,6}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$",message ="이메일 형식으로 작성해 주세요." )
    private String email;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$" , message = "영문/한글 1~20자 이내로 작성해 주세요.")
    private String fullname;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(MemberEnum.USER)
                .isUsed(true)
                .pwChgDate(LocalDateTime.now())
                .build();
    }


    public String toString() {
        return "JoinMemberReqDto(username=" + this.getName() +
                ", email=" + this.getEmail() +
                ", fullname=" + this.getFullname() +
                ")";
    }
}
