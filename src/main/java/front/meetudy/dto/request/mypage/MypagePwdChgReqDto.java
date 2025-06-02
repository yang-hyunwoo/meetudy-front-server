package front.meetudy.dto.request.mypage;

import front.meetudy.annotation.ValidationGroups;
import front.meetudy.annotation.customannotation.Password;
import lombok.Getter;

@Getter
public class MypagePwdChgReqDto {

    private String currentPw;

    @Password(message = "{password.pattern}", groups = ValidationGroups.Step2.class)
    private String newPw;
}
