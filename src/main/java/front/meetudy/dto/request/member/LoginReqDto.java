package front.meetudy.dto.request.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {

    private String email;

    private String password;

    private String chk;
}
