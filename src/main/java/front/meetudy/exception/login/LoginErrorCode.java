package front.meetudy.exception.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum LoginErrorCode {

    LG_MEMBER_ID_PW_INVALID(HttpStatus.UNAUTHORIZED, "ID 및 비밀번호를 확인해 주세요."),
    LG_PASSWORD_WRONG_LOCKED(HttpStatus.UNAUTHORIZED, "비밀번호 5회 오류로 인해 계정이 잠겼습니다."),
    LG_PASSWORD_DATE_OVER(HttpStatus.CONFLICT, "비밀번호를 변경한지 3개월이 지났습니다."),
    LG_DISABLED_MEMBER(HttpStatus.CONFLICT,"비활성화된 계정입니다."),
    LG_DORMANT_ACCOUNT(HttpStatus.CONFLICT,"휴면 계정입니다."),
    LG_ANOTHER_ERROR(HttpStatus.CONFLICT,"관리자에게 문의하세요."),
    LG_FIRST_LOGIN_ING(HttpStatus.CONFLICT, "로그인을 진행해 주세요.")
    ;

    private final HttpStatus status;
    private final String message;


    }
